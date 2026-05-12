from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from nltk.tokenize import sent_tokenize
import nltk
import langdetect


def ensure_nltk_resources():
    resources = ['punkt', 'punkt_tab']
    for r in resources:
        try:
            nltk.data.find(f'tokenizers/{r}')
        except LookupError:
            nltk.download(r, quiet=True)



def detect_language(text):
    
    lang_map = {
        "fr": "french", "de": "german", "es": "spanish",
        "it": "italian", "pt": "portuguese", "nl": "dutch",
        "en": "english", "ar": "arabic"
    }
    detected = langdetect.detect(text)
    language = lang_map.get(detected, "english")
    return language



def semantic_chunk(text, threshold=0.5, language="english"):
    
    model = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")
    language = detect_language(text)

    ensure_nltk_resources()

    # Tokenisation adaptée à la langue détectée
    try:
        sentences = sent_tokenize(text, language=language)

    except OSError:
        sentences = text.split(". ")

    if len(sentences) == 0:
        return [text]

    embeddings = model.encode(sentences)
    chunks, current_chunk = [], [sentences[0]]

    for i in range(1, len(sentences)):
        sim = cosine_similarity([embeddings[i-1]], [embeddings[i]])[0][0]
        if sim >= threshold:
            current_chunk.append(sentences[i])
        else:
            chunks.append(" ".join(current_chunk))
            current_chunk = [sentences[i]]

    chunks.append(" ".join(current_chunk))
    return chunks



