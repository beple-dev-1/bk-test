# PROGRESS.md — 작업 진행 내역

> CLAUDE.md는 불변 명세서. 이 파일은 실제 구현 진행 내역을 기록한다.

---

## 구현 완료 목록

### 1. 프로젝트 초기 구현 (커밋: `33c352f`)

#### 공통 인프라
- `CryptoUtils.java` — AES-256 GCM 암복호화 유틸 (`common/crypto`)
- `CryptoProperties.java` — 암호화 키 로딩 (`config`)
- `BpayClient.java` — 비플페이 API 공통 HTTP 클라이언트 (`client/bpay`)
  - COMM 생성 → JSON 직렬화 → AES-GCM 암호화 → POST → 복호화 → 역직렬화
  - TNO: `ORG_ID(13) + REQ_DTTM(14) + J(요청지구분) + 랜덤(4)` = 32자
- `AppConfig.java` — `RestTemplate`, `ObjectMapper` Bean 등록

#### 공통 DTO (`dto/common`)
- `CommRequest` / `CommResponse` — 요청/응답 공통부
- `BpayApiRequest<T>` / `BpayApiResponse<T>` — COMM + DETAIL 래퍼
- `DataEnvelope` — `{"DATA": "..."}` HTTP 바디 래퍼
- `BpayCallResult<T>` — UI에 반환하는 통합 결과 객체
- `ApiResponse<T>` — 컨트롤러 공통 응답 래퍼

#### 순방향 API 구현 (이용기관 → 비플페이)

| # | API명 | 엔드포인트(내부) | 비플페이 URL |
|---|-------|----------------|------------|
| 1 | 회원여부 조회 | `POST /api/member/check` | `/bravo/v1/member/check` |
| 2 | 회원가입 및 수정 | `POST /api/member/join` | `/bravo/v1/member/join` |
| 3 | QR/BAR코드 생성 | `POST /api/payment/qr/create` | `/bravo/v1/pay/qr/create` |
| 4 | QR 검증 | `POST /api/payment/qr/verify` | `/bravo/v1/pay/qr/verify` |
| 5 | MPM 결제요청 | `POST /api/payment/mpm` | `/bravo/v1/pay/mpm` |
| 6 | 결제상태조회 (CPM/MPM) | `POST /api/payment/status` | `/bravo/v1/pay/status` |
| 7 | 결제내역 집계 조회 | `POST /api/settle/summary` | `/bravo/v1/settle/summary` |

#### 역방향 API 구현 (비플페이 → 이용기관)

실제 수신 엔드포인트 (`InboundApiController`) + 테스트 발신 프록시 (`TestReverseApiController`)

| # | API명 | 수신 URL | 비고 |
|---|-------|---------|------|
| 9 | 환불결과 통지 | `POST /api/bpy/v1/refund/noti` | |
| 10 | 포인트 거래결과 조회 | `POST /api/bpy/v1/tran/result` | |
| 11 | 포인트 차감/환불/망취소 요청 | `POST /api/bpy/v1/pay/request` | |
| 12 | CPM 결제완료 통지 | `POST /api/bpy/v1/cpm/complete` | |

- `ReverseApiService.java` — 수신 비즈니스 로직 (테스트용 RC=0000 정상 반환)
- `TestReverseApiController.java` — UI에서 역거래 API를 직접 발신하는 프록시
  - 목적지: `https://dev-gift.appply.co.kr`
  - TNO 요청지구분: `B` (비플페이 → 이용기관 방향 시뮬레이션)

#### UI (`templates/index.html`, `static/js/app.js`, `static/css/app.css`)
- 2컬럼 레이아웃: 좌(시나리오 패널) / 우(개발자 패널)
- **시나리오 API 테스트** 패널 (기본): CPM결제 / MPM결제 / 정산대사
- **역거래 API 테스트** 패널 (토글): 4개 역방향 API 폼
- 개발자 패널: 요청 Plain JSON / 암호화 DATA / 응답 암호화 DATA / 응답 Plain JSON / API 히스토리(최근 10건)
- API 오류 시 서버 상세 오류(`errorDetail`) 빨간 블록으로 표시
- 모든 UI 라벨 한글화, 각 기능별 API URI 표기

---

### 2. Railway 배포 설정 (커밋: `9f395ac` ~ `f1e6bb1`)

#### 변경 내용

| 항목 | 변경 전 | 변경 후 |
|------|--------|--------|
| 설정 파일 | `application.properties` | `application.yml` |
| 포트 설정 | 하드코딩 `8080` | `${PORT:8080}` (Railway 환경변수 지원) |
| 빌더 | — | Dockerfile (Nixpacks가 Gradle 9 미지원) |
| 암호화 키 로딩 | `crypto.properties` 필수 | 로컬: `crypto.properties`, Railway: `ENCRYPT_KEY` 환경변수 |

#### 추가 파일
- `railway.toml` — Dockerfile 빌더 지정, healthcheck 경로 설정
- `Dockerfile` — 멀티스테이지 빌드 (eclipse-temurin:17-jdk → eclipse-temurin:17-jre)

---

## 환경별 설정 관리

```
로컬                          GitHub                        Railway
────────────────              ──────────────────            ──────────────────
crypto.properties  --(제외)-> 소스코드 (키 없음) --(배포)-> 소스코드
encryptKey=***                                              + ENCRYPT_KEY (Variables)
```

- **민감정보는 GitHub에 올리지 않는다** — 키 값은 로컬 파일 또는 Railway Variables로만 관리
- `crypto.properties`는 `.gitignore` 처리

---

## 배포 현황

| 환경 | URL | 상태 |
|------|-----|------|
| 로컬 | `http://localhost:8080` | 정상 |
| Railway (개발) | Railway 제공 도메인 | 정상 배포 완료 |

---

## 미구현 / 제외 항목

| 항목 | 사유 |
|------|------|
| 결제내역 상세 조회 (`/bravo/v1/settle/list`) | 사용하지 않기로 결정 |
| 회원관리 UI | 개발 범위 제외 |
| 인증/로그인 | 개발 범위 제외 |
| 운영 환경 배포 | 개발 전용 앱 |
