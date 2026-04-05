# CLAUDE.md — 브라보코리아 비플페이 API 테스트 앱

## 프로젝트 목적

개발 전용 API 시나리오 테스트 애플리케이션이다.
비플페이-브라보코리아 결제연동 API 흐름을 UI로 시뮬레이션하는 용도이며, **운영 환경에 사용하지 않는다.**
Swagger 스타일 API 테스터가 아닌, **시나리오 기반 UI 테스트 앱**이다.

---

## 기술 스택

- **Backend**: Spring Boot
- **Template**: Thymeleaf
- **CSS**: Bootstrap
- **Script**: Minimal JavaScript (복잡한 JS 금지)
- **Util**: Lombok (camelCase 네이밍 사용)

---

## 아키텍처

```
com.example.bravokoria
├── controller      # Thymeleaf 뷰 컨트롤러 + API 호출 엔드포인트
├── service         # 비즈니스 로직 (암복호화 처리 금지)
├── client          # 비플페이 API 호출 공통 클라이언트
├── dto             # 요청/응답 DTO
└── common          # 암복호화, 직렬화, HTTP 래핑 공통 모듈
```

### 핵심 규칙
- **서비스 레이어에서 암복호화 처리 금지** — 반드시 `common` 모듈에서만 처리
- 공통 API 클라이언트(`client`)를 통해서만 외부 API 호출
- Lombok 사용, 네이밍은 camelCase

---

## 암복호화 스펙

모든 API 통신은 `DATA` 필드 하나로 주고받는다.

### 요청 흐름
1. COMM + DETAIL 객체 생성
2. JSON 문자열로 직렬화
3. AES-GCM 암호화
4. Base64 인코딩 → `DATA` 필드에 삽입
5. HTTP 요청 전송

```json
{ "DATA": "BASE64_AESGCM_ENCRYPTED_STRING" }
```

### 응답 흐름
1. HTTP 응답 수신
2. `DATA` 필드 추출
3. Base64 디코딩 → AES-GCM 복호화
4. JSON 역직렬화

### 암복호화 키 정보
- **암복호화키**: 브라보코리아에서 발급 (AES-GCM 사용)
- **APP_CD**: `B_JBK`
- **ORG_ID**: `PBP2511000011`

---

## 서버 정보

| 구분 | 비플페이 (호출 대상) | 브라보코리아 (수신 대상) |
|------|---------------------|------------------------|
| 개발 | https://dev-biz-zero.bizplay.co.kr | https://dev-gift.appply.co.kr |
| 운영 | https://zero.appplay.co.kr | https://gift.appply.co.kr |
| 포트 | 443 | 443 |

---

## 공통 메시지 구조

### 요청 공통부 (COMM)

| 필드 | 한글명 | 길이 | 필수 | 설명 |
|------|--------|------|------|------|
| APP_CD | APP 코드 | 20 | ● | `B_JBK` |
| ORG_ID | 이용기관ID | 13 | ● | `PBP2511000011` |
| TNO | 전문추적번호 | 32 | ● | ORG_ID(13) + REQ_DTTM(14) + 요청지구분(1) + 랜덤(4) |
| REQ_DTTM | 요청일시 | 14 | ● | yyyymmddhh24miss |

- **TNO 요청지구분**: `J` = JBB 요청, `B` = 비플페이 요청

### 응답 공통부 (COMM)

| 필드 | 한글명 | 길이 | 설명 |
|------|--------|------|------|
| APP_CD | APP 코드 | 20 | 요청값 그대로 반환 |
| ORG_ID | 이용기관ID | 13 | 요청값 그대로 반환 |
| TNO | 전문추적번호 | 32 | 요청값 그대로 반환 |
| RES_DTTM | 응답일시 | 14 | yyyymmddhh24miss |
| RC | 응답코드 | 4 | `0000` = 정상 |
| RM | 응답메시지 | 100 | |

---

## API 목록

### 이용기관 → 비플페이 (클라이언트가 호출)

| # | API명 | URL |
|---|-------|-----|
| 1 | 회원여부 조회 | `POST /bravo/v1/member/check` |
| 2 | 회원가입 및 수정 | `POST /bravo/v1/member/join` |
| 3 | QR/BAR코드 생성요청 | `POST /bravo/v1/pay/qr/create` |
| 4 | QR 검증 | `POST /bravo/v1/pay/qr/verify` |
| 5 | MPM 결제요청 | `POST /bravo/v1/pay/mpm` |
| 6 | 결제상태조회 | `POST /bravo/v1/pay/status` |
| 7 | 결제내역 집계 조회 | `POST /bravo/v1/settle/summary` |
| 8 | 결제내역 상세 조회 | `POST /bravo/v1/settle/list` |

### 비플페이 → 이용기관 (서버가 수신 구현)

| # | API명 | URL |
|---|-------|-----|
| 9 | 환불결과 통지 | `POST /api/bpy/v1/refund/noti` |
| 10 | 포인트 거래결과 조회 | `POST /api/bpy/v1/tran/result` |
| 11 | 포인트 차감/환불/망취소 요청 | `POST /api/bpy/v1/pay/request` |
| 12 | CPM 결제완료 통지 | `POST /api/bpy/v1/cpm/complete` |

---

## API 상세 명세

### 1. 회원여부 조회 — `POST /bravo/v1/member/check`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | 이용기관 회원코드 |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| JOIN_YN | 가입여부 | 1 | ● | Y/N |

---

### 2. 회원가입 및 수정 — `POST /bravo/v1/member/join`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| TYPE | 요청구분값 | 1 | ● | R=신규가입, U=정보업데이트, E=해지 |
| CI | 사용자CI | 88 | ○ | |
| MOB_NO | 핸드폰번호 | 11 | ○ | ex) 01000000000 |
| MEMB_NM | 이름 | 100 | ○ | |
| BRT_DT | 생년월일 | 8 | ○ | ex) 19801231 |
| GNDR | 성별 | 1 | ○ | 0=여성, 1=남성 |
| IN_FRN_TP | 내외국인구분 | 1 | ○ | 1=내국인, 2=외국인 |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 |
|------|--------|------|------|
| JOIN_YN | 가입여부 | 1 | ● |

---

### 3. QR/BAR코드 생성요청 — `POST /bravo/v1/pay/qr/create`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| PAY_TYPE | 결제수단 | 1 | ● | P=이용기관 자체 결제 포인트 |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| QR_CODE | QR코드 | 50 | ● | CPM 결제용 |
| BAR_CODE | 바코드 | 50 | ● | 바코드 앞자리 39 |
| EXP_TIME | 만료일시 | 14 | ● | 유효시간 170초, yyyymmddhh24miss |

---

### 4. QR 검증 — `POST /bravo/v1/pay/qr/verify`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| QR_CODE | QR코드 | 50 | ● | 가맹점 MPM QR 코드 |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| RES_CD | 개별응답코드 | 4 | ● | 0000=정상 |
| RES_MSG | 개별응답메시지 | 100 | ● | |
| TOKEN | QR검증토큰 | 50 | ○ | 정상 QR시 발급, MPM 결제요청에 사용 |
| AFLT_INFO | 가맹점정보 (Object) | | ○ | AFLT_ID, AFLT_NM, BIZ_NO, UPJONG_NM, REPR_NM, TEL_NO, ADDR, ADDR_DTL |

---

### 5. MPM 결제요청 — `POST /bravo/v1/pay/mpm`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| ORG_TNO | 원거래 전문추적번호 | 32 | ● | QR 검증 API 호출시 사용한 COMM.TNO |
| TOKEN | QR검증토큰 | 50 | ● | QR 검증 응답의 TOKEN |
| QR_CODE | QR코드 | 50 | ● | 가맹점 QR 코드 |
| PAY_TYPE | 결제수단 | 1 | ● | P=이용기관 자체 결제 포인트 |
| AMT | 결제금액 | 13 | ● | |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| RES_CD | 개별응답코드 | 4 | ● | |
| RES_MSG | 개별응답메시지 | 100 | ● | |
| PAY_INFO | 결제정보 (Object) | | ○ | TRX_DT, TRX_TM, TRX_SEQ, AFLT_ID/NM, AMT, SUPY_AMT, VAT, SVC_AMT, BIZ_NO, UPJONG_NM, REPR_NM, TEL_NO, ADDR, ADDR_DTL |

---

### 6. 결제상태조회 — `POST /bravo/v1/pay/status`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| TYPE | 조회유형 | 1 | ● | C=CPM(QR/바코드), M=MPM |
| ORG_TNO | 원거래 전문추적번호 | 32 | ● | C: QR생성 TNO, M: MPM결제 TNO |

**응답 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| RES_CD | 개별응답코드 | 4 | ● | 0000=결제완료, 0001=결제실패, 0002=결제대기중(바코드유효), 0003=바코드만료, 0004=결제진행중 |
| RES_MSG | 개별응답메시지 | 100 | ● | |
| PAY_INFO | 결제정보 (Object) | | ○ | TRX_DT, TRX_TM, TRX_SEQ, AFLT_ID/NM, AMT, SUPY_AMT, VAT, SVC_AMT, BIZ_NO, UPJONG_NM, REPR_NM, TEL_NO, ADDR, ADDR_DTL |

---

### 7. 결제내역 집계 조회 — `POST /bravo/v1/settle/summary`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| STR_DATE | 조회 시작일자 | 8 | ● | YYYYMMDD, 시작=종료 동일하게 |
| END_DATE | 조회 종료일자 | 8 | ● | YYYYMMDD |
| ORG_SUMMARY_INFO | 이용기관 집계내역 (Object) | | ● | TOT_PAY_CNT/AMT, TOT_RFND_CNT/AMT, TOT_CNCL_CNT/AMT, TOT_RFND_CNCL_CNT/AMT |

**응답 DETAIL**: RES_CD, RES_MSG, ORG_SUMMARY_INFO(요청값 echo), BP_SUMMARY_INFO(비플 집계)

---

### 8. 결제내역 상세 조회 — `POST /bravo/v1/settle/list`

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| STR_DATE | 조회 시작일자 | 8 | ● | YYYYMMDD, 최대 31일 |
| END_DATE | 조회 종료일자 | 8 | ● | YYYYMMDD |
| PAGE_SIZE | 페이지당 건수 | 4 | ○ | default 10, max 1000 |
| PAGE_NO | 페이지번호 | 5 | ○ | default 1 |

**응답 DETAIL**: RES_CD, RES_MSG, TOT_CNT, REC 리스트
- REC 필드: UUID, TRX_DT, TRX_SEQ, TNO, TYPE(P=결제/C=결제망취소/R=환불/B=환불망취소), PROC_ST(070001=정상/070002=불능/070003=처리중), AFLT_ID/NM, AMT, SUPY_AMT, VAT, SVC_AMT, BIZ_NO, UPJONG_NM, REPR_NM, TEL_NO, ADDR, ADDR_DTL

---

### 9. 환불결과 통지 — `POST /api/bpy/v1/refund/noti` *(이용기관 구현)*

비플페이가 호출 → 이용기관이 수신

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 |
|------|--------|------|------|
| TRGT_TRX_DT | 대상거래일자 | 8 | ● |
| TRGT_TRX_SEQ | 대상거래일련번호 | 20 | ● |
| ORG_TRX_DT | 원거래일자 | 8 | ● |
| ORG_TRX_SEQ | 원거래일련번호 | 20 | ● |
| AMT | 거래금액 | 13 | ● |
| UUID | 유저고유식별ID | 100 | ● |

**응답 DETAIL**: RES_CD, RES_MSG

> v1.0.9 이후 환불 통지만 수행 (결제망취소 통지 제거됨)

---

### 10. 포인트 거래결과 조회 — `POST /api/bpy/v1/tran/result` *(이용기관 구현)*

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| TRGT_TRX_DT | 대상거래일자 | 8 | ● | 비플 거래일자 |
| TRGT_TRX_SEQ | 대상거래일련번호 | 20 | ● | 비플 거래일련번호 |
| ORG_TRX_DT | 원거래일자 | 8 | ○ | 환불/차감망취소 시 필수 |
| ORG_TRX_SEQ | 원거래일련번호 | 20 | ○ | |
| TYPE | 조회대상 유형 | 1 | ● | 1=차감, 2=환불, 3=차감망취소(미사용), 4=환불망취소(미사용) |
| UUID | 유저고유식별ID | 100 | ● | |

**응답 DETAIL**: RES_CD, RES_MSG, AMT(선택), BALANCE(선택)

> v1.0.9: TYPE 3,4(망취소)는 2026.03.30 협의로 미사용

---

### 11. 포인트 차감/환불/망취소 요청 — `POST /api/bpy/v1/pay/request` *(이용기관 구현)*

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| TRX_DT | 거래일자 | 8 | ● | 비플 거래일자 |
| TRX_SEQ | 거래일련번호 | 20 | ● | 비플 거래일련번호 |
| MPM_TNO | MPM전문추적번호 | 32 | ○ | TYPE=1(차감MPM)인 경우만 |
| ORG_TRX_DT | 원거래일자 | 8 | ○ | 환불/망취소 시 필수 |
| ORG_TRX_SEQ | 원거래일련번호 | 20 | ○ | |
| AMT | 거래금액 | 13 | ● | |
| UUID | 유저고유식별ID | 100 | ● | |
| AFLT_ID | 가맹점ID | 20 | ● | |
| AFLT_NM | 가맹점명 | 100 | ● | |
| TYPE | 유형 | 1 | ● | 1=차감(MPM), 2=차감(CPM), 3=환불, 4=차감망취소, 5=환불망취소 |

**응답 DETAIL**: RES_CD, RES_MSG, AMT(선택), BALANCE(선택)

---

### 12. CPM 결제완료 통지 — `POST /api/bpy/v1/cpm/complete` *(이용기관 구현)*

**요청 DETAIL**
| 필드 | 한글명 | 길이 | 필수 | 비고 |
|------|--------|------|------|------|
| UUID | 유저식별고유ID | 100 | ● | |
| ORG_TNO | 원거래 전문추적번호 | 32 | ● | QR/바코드 생성 API의 TNO |
| PAY_INFO | 결제정보 (Object) | | ● | TRX_DT, TRX_TM, TRX_SEQ, AFLT_ID/NM, AMT, SUPY_AMT, VAT, SVC_AMT, BIZ_NO, UPJONG_NM, REPR_NM, TEL_NO, ADDR, ADDR_DTL, PROC_ST(070001=정상/070002=불능) |

**응답 DETAIL**: RES_CD, RES_MSG

> 정상/실패 결제 모두 통지 (v1.0.8). 이용기관 응답에 관계없이 1회만 발생.

---

## UI 구조

2컬럼 고정 레이아웃 (design_sample.png 기준)

### 왼쪽 패널 (사용자 인터랙션 영역)
- **계정 선택 카드**: 하드코딩된 계정 목록, 선택 시 UUID 저장
- **CPM 결제 테스트 카드**: QR 생성 버튼 + QR 이미지 표시 + 결제상태 조회
- **MPM 결제 테스트 카드**: QR 코드 입력 + QR 검증 + MPM 결제 요청 + 결제상태 조회
- **정산 대사 카드**: 집계/상세 조회

### 오른쪽 패널 (개발자 패널)
**API 요약**
- apiName, url, method, selectedUuid, timestamp, status

**요청 정보**
- Plain JSON (복호화 전)
- Encrypted DATA (암호화 후)
- Final HTTP Body

**응답 정보**
- Raw 응답
- Encrypted DATA
- Plain JSON
- 요약

**시나리오 상태**
- lastQrVerifyTno, nextMpmOrgTno, lastQrVerifyToken, lastQrCode

**API 히스토리**: 최근 10건

---

## 상태 관리

```
selectedAccountUuid
selectedAccountName
cpmQrCode
cpmCreateRequestTno
lastQrVerifyTno
lastQrVerifyToken
lastVerifiedQrCode
lastMpmPaymentTno
lastApiName
lastRequestPlainJson
lastRequestEncryptedData
lastResponseEncryptedData
lastResponsePlainJson
```

---

## CPM 결제 플로우

```
1. QR/BAR코드 생성 API 호출
2. 응답의 QR_CODE를 이미지로 렌더링하여 표시
3. 외부(가맹점 단말)에서 QR 스캔 (앱 내에서 처리 불가)
4. 결제상태조회 API 호출 (TYPE=C, ORG_TNO=QR생성시 TNO)
```

**규칙**
- QR_CODE는 반드시 이미지로 렌더링
- BAR_CODE는 화면에 렌더링하지 않음
- QR이 결제 트리거

---

## MPM 결제 플로우 (순서 엄수)

```
1. QR 검증 API 호출 → COMM.TNO 저장
2. MPM 결제요청 API 호출
   - DETAIL.ORG_TNO = 저장한 QR 검증 COMM.TNO
   - DETAIL.TOKEN = QR 검증 응답의 TOKEN
3. 결제상태조회 API 호출 (TYPE=M)
```

**규칙**
- QR 검증 전에는 결제요청 버튼 비활성화
- QR 변경 시 상태 초기화

---

## 계정 설정

- 하드코딩된 계정 목록 사용 (인증 없음)
- 선택된 계정의 UUID가 모든 API 호출에 사용됨

---

## 개발 범위

### 포함
- 계정 선택
- CPM 결제 플로우
- MPM 결제 플로우
- 결제상태조회
- 정산 대사 (집계/상세)
- 개발자 패널 (요청/응답 로그, 암복호화 데이터)

### 제외
- 인증/로그인
- 운영 로직
- 회원관리 UI
