from datetime import datetime
from sqlalchemy import Boolean, DateTime, Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.connection import Base


class User(Base):
    __tablename__ = "user"
    __table_args__ = {"schema": "ai_planner"}

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    google_token: Mapped[str] = mapped_column(String(256), nullable=False, unique=True)
    ivc_token: Mapped[str | None] = mapped_column(String(256), nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    status: Mapped[int] = mapped_column(Boolean, nullable=False)

    statuses: Mapped[list["Status"]] = relationship("Status", back_populates="user")


class Status(Base):
    __tablename__ = "status"
    __table_args__ = {"schema": "ai_planner"}

    status: Mapped[str] = mapped_column(String(12), primary_key=True)
    description: Mapped[str | None] = mapped_column(String(256), nullable=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False)

    user: Mapped["User"] = relationship("User", back_populates="statuses")