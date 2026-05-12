from dataclasses import dataclass


@dataclass
class FileUploadDTO:
    idFileUploaded: int
    path: str
    type: str


