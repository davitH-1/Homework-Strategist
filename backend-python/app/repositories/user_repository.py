from sqlalchemy.orm import Session

from app.models.user import User, Status


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_by_id(self, user_id: int) -> User | None:
        return self.db.query(User).filter(User.id == user_id).first()

    def get_by_google_token(self, google_token: str) -> User | None:
        return self.db.query(User).filter(User.google_token == google_token).first()

    def create(self, google_token: str, status: int, ivc_token: str | None = None) -> User:
        user = User(google_token=google_token, ivc_token=ivc_token, status=status)
        self.db.add(user)
        self.db.commit()
        self.db.refresh(user)
        return user

    def update_status(self, user_id: int, status: int) -> User | None:
        user = self.get_by_id(user_id)
        if not user:
            return None
        user.status = status
        self.db.commit()
        self.db.refresh(user)
        return user

    def get_status(self, user_id: int) -> list[Status]:
        return self.db.query(Status).filter(Status.user_id == user_id).all()

    def set_status(self, user_id: int, status: str, description: str | None = None) -> Status:
        record = Status(status=status, description=description, user_id=user_id)
        self.db.merge(record)
        self.db.commit()
        return record