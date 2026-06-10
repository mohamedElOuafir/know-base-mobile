import os
from dotenv import load_dotenv
import psycopg2
from pgvector.psycopg2 import register_vector
from psycopg2.extras import execute_batch
from src.model.chunk import Chunk

BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../")
)

load_dotenv(os.path.join(BASE_DIR, ".env"))


class VectorDB:

    def __init__(self):
        self.db = psycopg2.connect(
            host=os.environ['DB_HOST'],
            database=os.environ['DB_NAME'],
            user=os.environ['DB_USERNAME'],
            password=os.environ['DB_PASSWORD'],
        )
        self.cursor = self.db.cursor()
        register_vector(self.db)


    def save_chunk(self, chunks: list[Chunk]):

        execute_batch(
            self.cursor,
            "INSERT INTO chunk (content, embedding, id_file_uploaded) VALUES (%s, %s, %s)",
            [
                (chunk.content, chunk.embedding.tolist(), chunk.id_file_uploaded)
                for chunk in chunks
            ]
        )
        self.db.commit()


    def similarity_search(self, query_embedding):
        print(query_embedding)
        self.cursor.execute(
            "SELECT content, id_file_uploaded FROM chunk ORDER BY embedding <=> %s::vector LIMIT 3",
            (query_embedding.tolist(),)
        )

        results = self.cursor.fetchall()
        chunks = []
        for result in results:
            new_chunk = Chunk(content=result[0], id_file_uploaded=result[1])
            chunks.append(new_chunk)


        return chunks