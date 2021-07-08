#!/usr/bin/env python
import pandas as pd
import numpy as np
import pymysql
from ast import literal_eval
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from nltk.stem.snowball import SnowballStemmer
from surprise import Reader, Dataset, SVD
from surprise.model_selection import cross_validate
import sys
import warnings; warnings.simplefilter('ignore')

uid_input = sys.argv[1]
movie_input = sys.argv[2]

connection = pymysql.connect(
    host='localhost',
    user='root',
    password='',
    db='dv_db',
)

sql = "SELECT userId, movieId, rating FROM ratings"

with connection.cursor() as cursor:
    cursor.execute(sql)
    result = cursor.fetchall()
    
ratings = pd.DataFrame(result, columns =['userId', 'movieId', 'rating'])

sql = "SELECT id, movieId, genres, cast, keywords, director, title, vote_count, vote_average, year FROM movies WHERE id NOT IN (SELECT DISTINCT movieId from ratings where userId = "+uid_input+")"

with connection.cursor() as cursor:
    cursor.execute(sql)
    result = cursor.fetchall()

connection.close()

movies = pd.DataFrame(result, columns =['id', 'movieId', 'genres', 'cast', 'keywords', 'director', 'title', 'vote_count', 'vote_average', 'year'])

movies['cast'] = movies['cast'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])
movies['director'] = movies['director'].astype('str').apply(lambda x: str.lower(x.replace(" ", "")))

id_map = movies[['movieId', 'id']]
id_map = id_map.merge(movies[['title', 'id']], on='id').set_index('title')

indices_map = id_map.set_index('id')

movies = movies.reset_index()
titles = movies['title']
indices = pd.Series(movies.index, index=movies['title'])

s = movies.apply(lambda x: pd.Series(x['keywords']),axis=1).stack().reset_index(level=1, drop=True)
s.name = 'keyword'
s = s.value_counts()
s = s[s > 1]

def filter_keywords(x):
    words = []
    for i in x:
        if i in s:
            words.append(i)
    return words

stemmer = SnowballStemmer('english')

movies['keywords'] = movies['keywords'].apply(filter_keywords)
movies['keywords'] = movies['keywords'].apply(lambda x: [stemmer.stem(i) for i in x])
movies['keywords'] = movies['keywords'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])

movies['director'] = movies['director'].astype('str').apply(lambda x: str.lower(x.replace(" ", "")))
movies['director'] = movies['director'].apply(lambda x: [x,x, x])

movies['genres'] = movies['genres'].apply(lambda x: x.split(','))

movies['soup'] = movies['keywords'] + movies['cast'] + movies['director'] + movies['genres']
movies['soup'] = movies['soup'].apply(lambda x: ' '.join(x))

count = CountVectorizer(analyzer='word',ngram_range=(1, 2),min_df=0, stop_words='english')
count_matrix = count.fit_transform(movies['soup'])
cosine_sim = cosine_similarity(count_matrix, count_matrix)

svd = SVD()

reader = Reader()
data = Dataset.load_from_df(ratings[['userId', 'movieId', 'rating']], reader)

trainset = data.build_full_trainset()
svd.fit(trainset)

def hybrid(userId, title):
    idx = indices[title]
    #tmdbId = id_map.loc[title]['id']
    
    #movie_id = id_map.loc[title]['movieId']
    
    sim_scores = list(enumerate(cosine_sim[int(idx)]))
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
    sim_scores = sim_scores[0:100]
    movie_indices = [i[0] for i in sim_scores]
    
    recommendedMovies = movies.iloc[movie_indices][['title', 'vote_count', 'vote_average', 'year', 'id']]
    recommendedMovies['est'] = recommendedMovies['id'].apply(lambda x: svd.predict(userId, indices_map.loc[x]['movieId']).est)
    recommendedMovies = recommendedMovies.sort_values('est', ascending=False)
    return recommendedMovies


result = hybrid(uid_input, movie_input)
col_one_list = result['id'].tolist()
print(col_one_list)
