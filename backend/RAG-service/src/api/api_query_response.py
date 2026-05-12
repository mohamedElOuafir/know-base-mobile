from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware
from starlette.requests import Request
from src.embedding.chunks_embeddings import query_embedding
from src.generator.response_generator import generate_response
from src.vector_db.pgvector_db import VectorDB


app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)



@app.get("/query")
async def response_query(req: Request):
    body = await req.json()
    query = body["query"]

    # Initializing the DB connection
    db_connection = VectorDB()

    # embed the user question
    query_emb = query_embedding(query)

    # retrieving the top 3 chunks
    relevant_chunks = db_connection.similarity_search(query_emb)

    # response generation based on relevant content chunks
    response = generate_response(query, relevant_chunks)

    return {
        "response": response,
    }







