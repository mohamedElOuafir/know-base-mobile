import google.generativeai as genai
import os
from dotenv import load_dotenv



BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../")
)

load_dotenv(os.path.join(BASE_DIR, ".env"))


genai.configure(api_key=os.environ['GEMINI_API_KEY'])
model = genai.GenerativeModel(os.environ['GEMINI_MODEL'])



def generate_response(question, retrieved_chunks):
    context = "\n\n".join([chunk.content for chunk in retrieved_chunks])

    prompt = f"""
    Use the following context to answer the question.
    If the answer is not in the context, say "I apologize, but the provided text focuses on [the context subject] and does not contain any information about [the question subject]. Therefore, I cannot answer your question based on the given context..", and change between brackets with related subjects.

    Context:
    {context}

    Question: {question}
    """

    response = model.generate_content(prompt)
    return response.text



