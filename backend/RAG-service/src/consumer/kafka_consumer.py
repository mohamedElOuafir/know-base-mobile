import os
from dotenv import load_dotenv
from kafka import KafkaConsumer
import json
from src.docs_loader.loading_docs import document_extractor
from src.embedding.chunks_embeddings import chunks_embeddings
from src.model.chunk import Chunk
from src.model.file_upload_dto import FileUploadDTO
from src.processor.chunking_docs import semantic_chunk
from src.vector_db.pgvector_db import VectorDB

BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../")
)

load_dotenv(os.path.join(BASE_DIR, ".env"))


db_connection = VectorDB()

Consumer = KafkaConsumer(
    'file-uploaded-topic',
    bootstrap_servers=[os.environ['KAFKA_BOOTSTRAP_SERVER']],
    value_deserializer=lambda m: json.loads(m.decode('utf-8')),
)


for msg in Consumer:
    event = msg.value

    upload_file = FileUploadDTO(
        event['idFileUploaded'],
        event['path'],
        event['type'],
    )

    extracted_text = document_extractor(upload_file.path, upload_file.type)

    chunks = semantic_chunk(extracted_text)

    embeddings_list = chunks_embeddings(chunks)

    chunk_list = []
    for i in range(0, len(embeddings_list)):
        new_chunk = Chunk(
            chunks[i],
            embeddings_list[i].embedding,
            upload_file.idFileUploaded
        )
        chunk_list.append(new_chunk)

    db_connection.save_chunk(chunk_list)

    print("chunks added successfully")




