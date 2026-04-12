import os

from ollama import chat, ChatResponse

MODEL_ID = os.getenv("OLLAMA_MODEL_ID", "gemma3:4b")


class GemmaModel:
    def __init__(self, model_id: str = MODEL_ID):
        self.model_id = model_id

    def generate(self, prompt: str) -> str:
        response: ChatResponse = chat(
            model=self.model_id,
            messages=[{"role": "user", "content": prompt}],
        )
        return response.message.content