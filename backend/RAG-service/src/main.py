import uvicorn


if __name__ == "__main__":
    uvicorn.run("src.api.api_query_response:app", host="localhost", port=8081)