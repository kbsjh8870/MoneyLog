# 머니로그 회고

## 프로젝트 한 줄 요약

7일 동안 Spring Boot 백엔드부터 React 프론트, Docker/GitHub Actions/EC2 배포까지 1인으로 관통한 개인 가계부 서비스.

## 🟢 Keep

- Jwt를 이전 실습들 참고해가며 구현했는데 완벽하지는 않지만 잘 동작하는 것 같다.
- QueryDSL, JQPL 적용을 나름대로 잘한 것 같다.
- JwtAuthenticationFilter를 구현하면서 AuthenticationProvider, Manager를 안쓰니 굳이 UserDetails가 필요없는것 같긴한데 나중에 확장할 가능성도 있고 학습 복기용으로도 일단 작성한 것은 좋았다고 생각한다.
- README를 문서 가이드 기준으로 다시 쓰면서 실제 배포 서버에 데모 계정을 만들어 로그인까지 검증해두었다.

## 🔴 Problem

- CORS/Security 화이트리스트를 한 번에 제대로 못 세우고 두 번에 걸쳐 수정을 했다: 처음엔 `CorsConfigurationSource` 자체가 없어서 모든 요청이 `Failed to fetch`였고, 붙인 뒤엔 `allowedMethods`에 `PATCH`가 빠져서 비밀번호 변경이 막혔다. 인증이 필요 없는/필요한 경로를 처음에 표로 정리하고 시작했으면 세 번 다 안 겪었을 문제라고 생각한다.
- `CategoryRequest`에 `@Setter`가 없어서 GET 쿼리 바인딩이 조용히 실패해 카테고리 목록이 항상 빈 배열로 나왔다. @RequestBody를 깜빡하고 안써서 기본값으로 @ModelAttribute로 바인딩되는데 이때 setter가 필요하다. 에러 로그 없이 그냥 빈 결과로 나오는 버그라 원인 찾는 데 시간이 조금 걸렸다.
- `TransactionService`에 `@Transactional`을 안 붙여서 `open-in-view: false` 환경에서 `category.getName()` 접근 시 `LazyInitializationException`이 발생했다. 세션 종료 이후 지연 로딩을 건드리는 문제라는 걸 알기 전까진 왜 되던 게 갑자기 안 되는지 찾는데 시간을 좀 썼다.
- 배포 초기에 `docker-compose.yml`의 DB URL에 `moenylog`라는 오타가 있어서 "Access denied"만 보고 권한 문제인 줄 알고 삽질했다.
- `ddl-auto: validate`를 prod에 그대로 뒀는데 마이그레이션 도구(Flyway 등)가 없어서, 깨끗한 MySQL에 첫 배포하자마자 테이블이 없다고 실패했다. 임시방편으로 `update`로 바꿔서 넘어갔는데 후에 정석 방식인 Flyway를 공부해서 바꿔봐야할것 같다.
- EC2 첫 배포에서 `~/moneylog` 디렉토리가 없는 상태로 배포 스크립트가 돌아 실제로는 아무것도 안 됐는데도 워크플로가 "성공"으로 떴다. 스크립트에 `set -e`가 없어서 `cd`가 조용히 실패해도 뒤 명령이 계속 실행되고, 마지막 `docker image prune`이 성공하니 잡(job) 전체가 초록불이었던 것.
- 처음에 EC2 스토리지를 기본 8G로 설정해서 이미지 몇 번 빌드하다 "no space left on device"로 용량 부족으로 인해 배포가 막혔다. 그래서 ec2 페이지에서 스토리지 증설 후 EBS 볼륨 리사이즈(`growpart` → `resize2fs`)까지 해야 했다.

## 🟡 Try

- Security 설정에서 "인증 없이 접근 가능한 경로" 목록을 기능 설계 단계에서 먼저 표로 정리하고 시작한다 (로그인/회원가입/재발급/Swagger 전체 경로 포함)
- 배포 스크립트는 처음부터 `set -e`를 넣어서, 실패가 조용히 넘어가 "성공"으로 보고되는 일이 없게 한다.
- 프론트/백엔드 DTO 계약(필드명)을 API 스펙 문서에 먼저 못 박고 양쪽 다 그 문서를 보고 구현한다.
- EC2 볼륨 크기·보안그룹 인바운드 규칙(CI 러너 IP 대역 포함)을 배포 전에 여유 있게 잡아둔다.

## 📚 배운 것

- JWT 인증의 전체 흐름(로그인 시 access+refresh 발급 → refresh는 DB에도 저장 → `/reissue`가 서명·만료·DB 존재 여부까지 검증 → 로그아웃 시 DB row 삭제로 JWT 자체 만료 전이라도 서버가 재발급을 거부)을 직접 구현을 통한 복기.
- 본인 데이터만 인가는 컨트롤러가 아닌 쿼리 레벨(`findByIdAndUserId`, `WHERE user_id = ?`)에서 걸어야 한다는 것.
- `docker compose up`은 `image:`가 로컬에 있으면 `build:`가 있어도 재빌드를 건너뛴다 — 로컬 테스트 중 코드를 고쳤는데 반영이 안 되는 걸 겪고 나서 `image`/`build` 우선순위를 정확히 알게 됨.
- GitHub Actions의 `workflow_run` 트리거는 `github.event.workflow_run.head_sha`를 써야 하고, `GITHUB_TOKEN` 권한(`permissions:`)은 잡(job)마다 따로 선언해야 한다는 것.
- 커밋 메시지에 `[skip ci]`를 넣으면 `push` 트리거 워크플로를 안돌게 할 수 있음.

## 🚧 가장 크게 막혔던 지점

### 1. EC2 배포가 "성공"으로 뜨는데 실제로는 아무것도 안 뜸

**문제 상황**: EC2에 처음 배포했을 때 GitHub Actions는 매번 "성공"으로 끝났는데 실제로는 서버가 하나도 안 떠 있었다.

**원인**: 배포 스크립트가 `cd ~/moneylog`부터 시작하는데, 그 디렉토리를 EC2에 미리 만들어두지 않아 `cd`가 실패. 그런데 스크립트에 `set -e`가 없어서 이후의 `docker compose pull`, `up -d` 명령이 (아예 실패한 채로) 계속 실행됐고, 스크립트의 마지막 줄인 `docker image prune -af`는 어느 디렉토리에서 실행하든 항상 성공하기 때문에 SSH 액션 전체가 exit code 0으로 끝나 워크플로가 초록불로 표시됐다.

**해결**: `mkdir -p ~/moneylog`로 디렉토리를 만들고 `.env` 파일을 한 번 수동으로 옮긴 뒤, 배포 스크립트 맨 앞에 `set -e`를 추가해 이후 어떤 명령이든 실패하면 그 즉시 잡 전체가 실패로 보고되도록 수정함.

이 일을 겪고 나서야 "배포가 성공했다"는 CI 초록불이 실제로는 "마지막 명령이 성공했다"는 뜻일 뿐, 중간 단계가 다 통과했다는 보장이 아니라는 걸 체감했다. 그 뒤로 이어진 GHCR unknown blob(→ buildx 드라이버 문제), 디스크 풀(→ EBS 6.7G가 너무 작음 → 20G로 리사이즈), docker-compose 스코프 문제(→ `frontend` 서비스가 아직 이미지도 없는데 같이 띄우려다 실패) 도 전부 같은 배포 파이프라인 안에서 연쇄적으로 튀어나온 문제였는데, `set -e`로 실패를 조기에 드러내도록 고친 뒤부터는 "어디서 막혔는지"가 로그에 바로 찍혀서 진단 속도가 확실히 빨라졌다.

### 2. CSV export 구현 중 `Pageable.unpaged()`에 대한 오류 발생

**문제 상황**: 거래내역 CSV 내보내기를 만들다가, 기존 페이징 목록 조회에 쓰던 QueryDSL `search()` 메서드를 그대로 재사용해서 "페이징 없이 전체 조회"를 하려고 `Pageable.unpaged()`를 넘겼는데 `UnsupportedOperationException` 오류 발생.

**원인**: `search()` 내부 QueryDSL 쿼리가 필터 조건과 무관하게 항상 `.offset(pageable.getOffset()).limit(pageable.getPageSize())`를 호출하도록 짜여 있었는데, Spring Data의 `Pageable.unpaged()`(`Unpaged.INSTANCE`)는 `getOffset()`은 0을 정상 반환하지만 `getPageSize()`/`getPageNumber()`는 호출 시 예외를 던지도록 구현돼 있다. 페이징 없이 전체 조회로 넘긴 값이 페이징 파라미터를 무조건 쓰는 기존 코드와는 애초에 호환이 안 되는 조합이었던 것.

**해결**: `search()`에 `pageable.isPaged()`일 때만 `offset()/limit()`을 적용하도록 방어 코드를 추가해서 해결할 수 있을 것 같은데 다만 CSV export는 원래 우선순위도 낮은 기능이고 이를 적용할 프론트도 따로 만들어야하는 상황이라 기능을 마저 완성하기보다 범위에서 빼서 전부 되돌리고 다음 기회로 미뤘다.

## 🔮 더 만든다면

- F-11 월 예산 초과 경고를 실제 알림(이메일/푸시)까지
- F-12 통계 시각화를 파이 외에 기간별 추이(바/라인) 차트로 확장
- F-15 테스트 커버리지 확대 — 이번엔 시간 관계상, 테스트 작성 실력 이슈로 `-x test`로 배포했다
- F-16 무중단 배포 (현재는 `docker compose up -d`로 컨테이너를 내렸다 올리는 방식이라 배포 순간 잠깐 다운타임이 있다)
- jwt 토큰을 로컬스토리지에 저장중인데 이를 HttpOnly 쿠키로 리팩토링
