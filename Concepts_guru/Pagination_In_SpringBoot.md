Perfect use-case, Jay 👍
Pagination is **one of those things interviewers + production systems really care about**, especially when you’re dealing with **thousands / millions of records**.

Below is a **production-ready, end-to-end pagination design**:

* ✅ **Backend**: Spring Boot (Controller → Service → Repository)
* ✅ Uses **Spring Data JPA Pageable**
* ✅ Supports **sorting + filtering**
* ✅ Avoids common performance pitfalls
* ✅ **Frontend**: Angular (RxJS + HttpClient + best practices)

---

# 1️⃣ Database Design (Employee Table)

```sql
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    emp_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    department VARCHAR(50),
    designation VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_employee_department ON employee(department);
CREATE INDEX idx_employee_created_at ON employee(created_at);
```

✔ Indexes matter for pagination performance.

---

# 2️⃣ JPA Entity (Employee)

```java
@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empCode;
    private String name;
    private String email;
    private String department;
    private String designation;

    private LocalDateTime createdAt;
}
```

---

# 3️⃣ Repository Layer (Production-grade)

### ✅ Extend `JpaRepository`

```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByDepartment(
        String department,
        Pageable pageable
    );
}
```

✔ `Pageable` automatically handles:

* LIMIT
* OFFSET
* SORT

---

# 4️⃣ DTOs (Never expose Entity directly)

### EmployeeResponseDTO

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private String empCode;
    private String name;
    private String email;
    private String department;
    private String designation;
}
```

### PaginatedResponseDTO (Generic & reusable)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
```

---

# 5️⃣ Service Layer (Business Logic + Mapping)

```java
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public PageResponse<EmployeeResponseDTO> getEmployees(
            int page,
            int size,
            String sortBy,
            String direction,
            String department
    ) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeePage;

        if (department != null && !department.isEmpty()) {
            employeePage = employeeRepository.findByDepartment(department, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeResponseDTO> content = employeePage
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .toList();

        return new PageResponse<>(
                content,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
    }

    private EmployeeResponseDTO mapToDTO(Employee e) {
        return new EmployeeResponseDTO(
                e.getId(),
                e.getEmpCode(),
                e.getName(),
                e.getEmail(),
                e.getDepartment(),
                e.getDesignation()
        );
    }
}
```

✔ Clean separation
✔ Easy to unit test
✔ Future-proof for filters

---

# 6️⃣ Controller Layer (REST API)

```java
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponseDTO>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String department
    ) {

        return ResponseEntity.ok(
                employeeService.getEmployees(
                        page, size, sortBy, direction, department
                )
        );
    }
}
```

### 🔗 API Example

```
GET /api/employees?page=0&size=20&sortBy=name&direction=ASC
```

---

# 7️⃣ Performance Best Practices (Important 🔥)

✅ Use **DTO projection**
✅ Avoid `SELECT *`
✅ Always sort on **indexed columns**
✅ Never return full entity
✅ Keep `size` capped (e.g. max 100)

```java
size = Math.min(size, 100);
```

---

# 8️⃣ Angular Frontend (Production-Ready)

---

## 8.1 Employee Model

```ts
export interface Employee {
  id: number;
  empCode: string;
  name: string;
  email: string;
  department: string;
  designation: string;
}
```

---

## 8.2 Page Response Model

```ts
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
```

---

## 8.3 Employee Service (Angular)

```ts
@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly API_URL = '/api/employees';

  constructor(private http: HttpClient) {}

  getEmployees(
    page: number,
    size: number,
    sortBy = 'createdAt',
    direction = 'DESC'
  ) {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<PageResponse<Employee>>(this.API_URL, { params });
  }
}
```

---

## 8.4 Component Logic (Clean & Reactive)

```ts
export class EmployeeListComponent implements OnInit {

  employees: Employee[] = [];
  page = 0;
  size = 10;
  totalElements = 0;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employeeService.getEmployees(this.page, this.size)
      .subscribe(res => {
        this.employees = res.content;
        this.totalElements = res.totalElements;
      });
  }

  onPageChange(event: any) {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.loadEmployees();
  }
}
```

---

## 8.5 HTML (Angular Material – Production Standard)

```html
<table mat-table [dataSource]="employees">

  <ng-container matColumnDef="name">
    <th mat-header-cell *matHeaderCellDef>Name</th>
    <td mat-cell *matCellDef="let e">{{ e.name }}</td>
  </ng-container>

  <tr mat-header-row *matHeaderRowDef="['name']"></tr>
  <tr mat-row *matRowDef="let row; columns: ['name'];"></tr>
</table>

<mat-paginator
  [length]="totalElements"
  [pageSize]="size"
  [pageSizeOptions]="[5, 10, 20]"
  (page)="onPageChange($event)">
</mat-paginator>
```

---

# 9️⃣ Interview-Ready Explanation (Use This 💡)

> We use **server-side pagination** using Spring Data JPA `Pageable` to efficiently fetch data in chunks.
> The backend returns metadata like `totalElements` and `totalPages`, which the Angular frontend uses to render pagination controls.
> This avoids loading large datasets into memory and scales well for thousands of records.

---
