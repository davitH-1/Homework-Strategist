from fastapi import FastAPI
from app.api.routes import plan

app = FastAPI()

app.include_router(plan.router)