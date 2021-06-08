import pandas as pd
import numpy as np
import pymysql
from ast import literal_eval
import sys
import warnings; warnings.simplefilter('ignore')

genere_input = sys.argv[1]
#genere_input = "Science Fiction"
connection = pymysql.connect(
    host='localhost',
    user='root',
    password='',
    db='dv_db',
)

sql = "SELECT id, movieId, genres, title, vote_count, vote_average, year, popularity FROM movies"

with connection.cursor() as cursor:
    cursor.execute(sql)
    result = cursor.fetchall()

connection.close()

movies = pd.DataFrame(result, columns =['id', 'movieId', 'genres', 'title', 'vote_count', 'vote_average', 'year', 'popularity'])
movies['genres'] = movies['genres'].apply(lambda x: x.split(','))

s = movies.apply(lambda x: pd.Series(x['genres']),axis=1).stack().reset_index(level=1, drop=True)
s.name = 'genre'
gen_md = movies.drop('genres', axis=1).join(s)

def build_chart(genre, percentile=0.75):
    df = gen_md[gen_md['genre'] == genre]
    vote_counts = df[df['vote_count'].notnull()]['vote_count'].astype('int')
    vote_averages = df[df['vote_average'].notnull()]['vote_average'].astype('int')
    C = vote_averages.mean()
    m = vote_counts.quantile(percentile)
    
    qualified = df[(df['vote_count'] >= m) & (df['vote_count'].notnull()) & (df['vote_average'].notnull())][['id', 'title', 'year', 'vote_count', 'vote_average', 'popularity']]
    qualified['vote_count'] = qualified['vote_count'].astype('int')
    qualified['vote_average'] = qualified['vote_average'].astype('int')
    
    qualified['wr'] = qualified.apply(lambda x: (x['vote_count']/(x['vote_count']+m) * x['vote_average']) + (m/(m+x['vote_count']) * C), axis=1)
    qualified = qualified.sort_values('wr', ascending=False).head(10)
    
    return qualified

result = build_chart(genere_input)

col_one_list = result['id'].tolist()
print(col_one_list)