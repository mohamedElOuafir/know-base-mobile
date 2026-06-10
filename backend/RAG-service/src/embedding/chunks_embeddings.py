from sentence_transformers import SentenceTransformer
from src.model.chunk import Chunk

model = SentenceTransformer('all-MiniLM-L6-v2')

def chunks_embeddings(chunks):
    embeddings_list = model.encode(chunks)

    chunk_emb_list = []

    for i in range(0, len(embeddings_list)):
        new_chunk = Chunk(chunks[i], embeddings_list[i])
        chunk_emb_list.append(new_chunk)

    return chunk_emb_list


def query_embedding(query):

    query_emb = model.encode(query)

    return query_emb




