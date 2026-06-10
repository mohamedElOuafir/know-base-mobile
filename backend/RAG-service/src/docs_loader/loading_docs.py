import requests
import tempfile
import os

from langchain_community.document_loaders import (
    PyPDFLoader,
    Docx2txtLoader,
    UnstructuredFileLoader
)


def document_extractor(document_url, doc_type):

    response = requests.get(document_url)

    if response.status_code != 200:
        raise Exception(
            f"Impossible de télécharger le fichier : {response.status_code}"
        )

    suffix = ".tmp"

    if doc_type == 'application/pdf':
        suffix = ".pdf"

    elif (
        doc_type == 'application/msword'
        or
        doc_type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    ):
        suffix = ".docx"

    with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as temp_file:
        temp_file.write(response.content)
        temp_path = temp_file.name

    try:

        if doc_type == 'application/pdf':
            loader = PyPDFLoader(temp_path)

        elif (
            doc_type == 'application/msword'
            or
            doc_type == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        ):
            loader = Docx2txtLoader(temp_path)

        else:
            loader = UnstructuredFileLoader(temp_path)

        documents = loader.load()

        extracted_text = ""

        for doc in documents:
            extracted_text += doc.page_content + "\n"

        return extracted_text

    finally:
        os.remove(temp_path)