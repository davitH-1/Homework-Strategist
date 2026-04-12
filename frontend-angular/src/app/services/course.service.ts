import { Injectable } from '@angular/core';
import { Observable, of, delay } from 'rxjs';

export interface Course {
  id: number;
  name: string;
  course_code: string;
  enrollment_term_id: number;
  // This field can now be a string URL or null for courses without a banner
  image_download_url: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  /**
   * MOCK DATA: Updated with your actual terminal output.
   * Note: Canvas IDs are very large, so we ensure the interface handles them.
   */
  private mockData: Course[] = [
    {
      "id": 100770000000064974,
      "name": "23703 NUTR 210HF Honors Human Nutrition",
      "course_code": "23703 NUTR 210HF",
      "enrollment_term_id": 100770000000000268,
      "image_download_url": "https://images.unsplash.com/photo-1517260911058-0fcfd733702f?ixid=Mnw3NTYyOXwwfDF8c2VhcmNofDR8fHBpbmVhcHBsZXxlbnwxfDB8fHwxNjQyMzc4ODQ2&ixlib=rb-1.2.1&utm_medium=referral&utm_source=canvas-prod&w=262&h=146&crop=faces%2Centropy&fit=crop&fm=jpg&cs=tinysrgb&q=80"
    },
    {
      "id": 204830000000018840,
      "name": "Computer Organization and Assembly Language II - 31310/31311",
      "course_code": "202630_CS40B_31310/31311",
      "enrollment_term_id": 204830000000000161,
      "image_download_url": "https://inst-fs-iad-prod.inscloudgate.net/files/74077d86-688b-4181-a20c-586247b249c1/cpu%2040b.png?download=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3NzU4MTA1NzEsInVzZXJfaWQiOm51bGwsInJlc291cmNlIjoiL2ZpbGVzLzc0MDc3ZDg2LTY4OGItNDE4MS1hMjBjLTU4NjI0N2IyNDljMS9jcHUlMjA0MGIucG5nIiwiaG9zdCI6bnVsbCwiZXhwIjoxNzc2NDE1MzcxfQ.3G8dUSDlkpIJNwAZ-kicSb7RW-DYzVyqDSVyMGD5eBytLIWFQu9rXZJ6ywXkN9cKK0k2cFBJ4GiJAqK6r6D0hw"
    },
    {
      "id": 60000000000030642,
      "name": "DATA STRUCTURES - 32795",
      "course_code": "202630_CS1D_32795",
      "enrollment_term_id": 60000000000000238,
      "image_download_url": null
    },
    {
      "id": 100770000000015552,
      "name": "Honors Students",
      "course_code": "Honors",
      "enrollment_term_id": 100770000000000001,
      "image_download_url": "https://inst-fs-iad-prod.inscloudgate.net/files/a0d3b16d-bc0f-47a0-843e-119db34cd5c6/honorshead1a.png?download=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3NzU3MDU0NDcsInVzZXJfaWQiOm51bGwsInJlc291cmNlIjoiL2ZpbGVzL2EwZDNiMTZkLWJjMGYtNDdhMC04NDNlLTExOWRiMzRjZDVjNi9ob25vcnNoZWFkMWEucG5nIiwiaG9zdCI6bnVsbCwiZXhwIjoxNzc2MzEwMjQ3fQ.pW4MTs4t6EFiC9siH0t9eQuYfIwviudcNBJad_awBl0hPGiMpIteSXGpYrBl6WzaWGZl--kIaqz2eO3B1foVPA"
    },
    {
      "id": 60000000000030638,
      "name": "INTRODUCTION TO COMPUTER SCIENCE III - 32791",
      "course_code": "202630_CS1C_32791",
      "enrollment_term_id": 60000000000000238,
      "image_download_url": null
    },
    {
      "id": 60000000000030087,
      "name": "INTRODUCTION TO JAVA FOR COMPUTER SCIENCE - 34469",
      "course_code": "202630_CS4A_34469",
      "enrollment_term_id": 60000000000000238,
      "image_download_url": null
    },
    {
      "id": 35463,
      "name": "IVC: Honors Program",
      "course_code": "IVC: Honors Program",
      "enrollment_term_id": 1,
      "image_download_url": "https://inst-fs-iad-prod.inscloudgate.net/files/4fdc6af0-ed42-4c08-8693-58e5358917bc/IVC%20Honors%20Medal.jpg?download=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3NzU3MjkwMTUsInVzZXJfaWQiOm51bGwsInJlc291cmNlIjoiL2ZpbGVzLzRmZGM2YWYwLWVkNDItNGMwOC04NjkzLTU4ZTUzNTg5MTdiYy9JVkMlMjBIb25vcnMlMjBNZWRhbC5qcGciLCJob3N0IjpudWxsLCJleHAiOjE3NzYzMzM4MTV9.fZ0x9mW2s3VTa85oIIfIeKN9p5HDhCkB1eoBe-z7KONmmB5ApNblouvXoPVZjkPzHjoyWJecIMxyCt6ztDWVrA"
    },
    {
      "id": 204830000000004623,
      "name": "Library Research Workshops",
      "course_code": "Library Research Workshops",
      "enrollment_term_id": 204830000000000001,
      "image_download_url": "https://inst-fs-iad-prod.inscloudgate.net/files/75e4ac9b-024d-4a36-917f-86c532b462dc/College%20Research%20Badges.v2.png?download=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3NzU3Mzg3ODgsInVzZXJfaWQiOm51bGwsInJlc291cmNlIjoiL2ZpbGVzLzc1ZTRhYzliLTAyNGQtNGEzNi05MTdmLTg2YzUzMmI0NjJkYy9Db2xsZWdlJTIwUmVzZWFyY2glMjBCYWRnZXMudjIucG5nIiwiaG9zdCI6bnVsbCwiZXhwIjoxNzc2MzQzNTg4fQ.FxEH_nDHFIzI5CXIEyK_HNt32gz0XZhiJnyf-MTXn84YA_3IUuib7OqD97NrLcng0r_h3B3kYEfudWb5w7buxg"
    }
  ];

  getCourses(): Observable<Course[]> {
    // Simulates a network delay for the tour app feel
    return of(this.mockData).pipe(delay(1000));
  }
}
