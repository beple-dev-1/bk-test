/**
 * BK Payment Test — Main JS
 */

/* =========================================
   State
   ========================================= */
const state = {
    selectedUuid: 'gkwns458',
    selectedAccountName: '계정 1',
    memberVerified: false
};

let logCount = 0;

/* =========================================
   Timestamp
   ========================================= */
function ts() {
    const d = new Date();
    const p = v => String(v).padStart(2, '0');
    return `${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}.${String(d.getMilliseconds()).padStart(3,'0')}`;
}

/* =========================================
   Log Panel helpers
   ========================================= */
function appendInfo(text) {
    const el = document.createElement('div');
    el.className = 'log-entry-info';
    el.innerHTML = `<span class="log-meta">[${ts()}]</span><span class="log-text">${esc(text)}</span>`;
    push(el);
}

function appendSection(label, type) {
    const el = document.createElement('div');
    el.className = `log-entry-section ${type || ''}`;
    el.textContent = label;
    push(el);
}

function appendKV(key, value, valueClass) {
    const el = document.createElement('div');
    el.className = 'log-entry-kv';
    const cls = valueClass ? ` ${valueClass}` : '';
    el.innerHTML = `<span class="log-kv-key">${esc(key)}</span><span class="log-kv-val${cls}">${esc(value ?? '')}</span>`;
    push(el);
}

function appendJson(label, jsonStr, type) {
    const el = document.createElement('div');
    el.className = `log-entry-block ${type === 'res' ? 'block-res' : ''}`;
    let formatted = jsonStr;
    try { formatted = JSON.stringify(JSON.parse(jsonStr), null, 2); } catch(e) {}
    el.innerHTML = `<div class="log-block-label">${esc(label)}</div><pre class="log-block-json">${esc(formatted)}</pre>`;
    push(el);
}

function appendEncrypted(label, value) {
    const el = document.createElement('div');
    el.className = 'log-entry-block block-enc';
    el.innerHTML = `<div class="log-block-label">${esc(label)}</div><p class="log-block-enc">${esc(value ?? '')}</p>`;
    push(el);
}

function appendError(text) {
    const el = document.createElement('div');
    el.className = 'log-entry-error';
    el.innerHTML = `<i class="bi bi-exclamation-circle me-1"></i>${esc(text)}`;
    push(el);
}

function appendErrorDetail(text) {
    const el = document.createElement('div');
    el.className = 'log-entry-block';
    el.style.borderLeftColor = '#ef4444';
    el.innerHTML = `<div class="log-block-label" style="color:#991b1b">오류 상세</div>`
                 + `<pre class="log-block-json" style="color:#991b1b;white-space:pre-wrap">${esc(text)}</pre>`;
    push(el);
}

function maybeLogErrorDetail(result) {
    if (result && result.errorDetail) {
        appendErrorDetail(result.errorDetail);
    }
}

function appendDivider() {
    const el = document.createElement('hr');
    el.className = 'log-divider';
    push(el);
}

function push(el) {
    const panel = document.getElementById('logPanel');
    panel.appendChild(el);
    panel.scrollTop = panel.scrollHeight;
    logCount++;
    document.getElementById('logCount').textContent = `${logCount} 건`;
}

function esc(s) {
    return String(s ?? '')
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

/* =========================================
   Badge helpers
   ========================================= */
function setBadge(id, text, type) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = text;
    el.className = `scenario-badge badge-${type}`;
}

/* =========================================
   Real API Call
   ========================================= */
async function callApi(method, url, payload) {
    const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: payload ? JSON.stringify(payload) : undefined
    });
    return await res.json();
}

/* =========================================
   Account Selection
   ========================================= */
// 계정 ID → DOM id 매핑 (특수문자 포함 UUID 대응)
const accountOptionIds = {
    'gkwns458':              'accountOpt_gkwns458',
    'F5lM32LAiGa0S+0QoyOGFA==': 'accountOpt_F5lM32LAiGa0S',
    '1W5PtXMj9lcOrf0X2jaHog==': 'accountOpt_1W5PtXMj9lcOrf0X',
    'PH1tkIDnuY0cOThA1HurYA==': 'accountOpt_PH1tkIDnuY0cOThA'
};

function selectAccount(uuid, name) {
    // 이전 선택 해제
    Object.values(accountOptionIds).forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        el.classList.remove('selected');
        const check = el.querySelector('.account-check');
        if (check) check.style.display = 'none';
    });

    // 선택된 계정 활성화
    const optId = accountOptionIds[uuid];
    if (optId) {
        const el = document.getElementById(optId);
        el.classList.add('selected');
        const check = el.querySelector('.account-check');
        if (check) check.style.display = '';
    }

    state.selectedUuid = uuid;
    state.selectedAccountName = name;
    document.getElementById('selectedUuidDisplay').textContent = uuid;
    appendInfo(`Account selected: ${name} (${uuid})`);
}

/* =========================================
   Member Join
   ========================================= */
async function runMemberJoin() {
    const uuid    = document.getElementById('join_uuid').value.trim();
    const type    = document.getElementById('join_type').value;

    if (!uuid) { appendError('UUID를 입력하세요.'); return; }
    if (!type) { appendError('TYPE을 선택하세요.'); return; }

    setBadge('badgeMemberJoin', '요청 중...', 'run');
    appendDivider();
    appendInfo(`회원가입 요청 — UUID: ${uuid}, TYPE: ${type}`);

    const payload = {
        uuid,
        type,
        mobNo:   document.getElementById('join_mobNo').value.trim()   || null,
        membNm:  document.getElementById('join_membNm').value.trim()  || null,
        brtDt:   document.getElementById('join_brtDt').value.trim()   || null,
        gndr:    document.getElementById('join_gndr').value           || null,
        inFrnTp: document.getElementById('join_inFrnTp').value        || null,
        ci:      document.getElementById('join_ci').value.trim()      || null,
    };

    try {
        const response = await callApi('POST', '/api/account/join', payload);
        const result = response.data;

        appendSection('Request  ·  POST /bravo/v1/member/join', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('URL:', result.url);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        appendKV('TNO:', result.tno);
        appendKV('RES_DTTM:', result.resDttm);
        if (result.detail) {
            appendKV('JOIN_YN:', result.detail.JOIN_YN,
                result.detail.JOIN_YN === 'Y' ? 'val-ok' : 'val-err');
        }
        maybeLogErrorDetail(result);

        setBadge('badgeMemberJoin', result.rc === '0000' ? '완료' : '실패',
                 result.rc === '0000' ? 'done' : 'error');

        if (result.rc !== '0000') {
            appendError(`회원가입 실패 — RC: ${result.rc}, RM: ${result.rm}`);
        }
    } catch (e) {
        setBadge('badgeMemberJoin', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    }
}

/* =========================================
   Step 3: Member Check
   ========================================= */
async function runMemberCheck() {
    if (!state.selectedUuid) {
        appendError('No account selected.');
        return;
    }

    setBadge('badgeAccount', '확인 중...', 'run');
    document.getElementById('btnCheckMember').disabled = true;
    appendDivider();
    appendInfo(`Starting member check for UUID: ${state.selectedUuid}`);

    try {
        const response = await callApi('POST', '/api/account/check', { uuid: state.selectedUuid });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/member/check', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('URL:', result.url);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        appendKV('TNO:', result.tno);
        appendKV('RES_DTTM:', result.resDttm);
        maybeLogErrorDetail(result);

        if (result.detail) {
            const joinYn = result.detail.JOIN_YN;
            appendKV('JOIN_YN:', joinYn, joinYn === 'Y' ? 'val-ok' : 'val-err');
        }

        if (result.rc === '0000') {
            setBadge('badgeAccount', '확인 완료', 'done');
            state.memberVerified = true;
            unlockNextSteps();
            appendInfo(`Member verified. UUID [${state.selectedUuid}] will be used in all subsequent API calls.`);
        } else {
            setBadge('badgeAccount', '실패', 'error');
            appendError(`Member check failed — RC: ${result.rc}, RM: ${result.rm}`);
        }

    } catch (e) {
        setBadge('badgeAccount', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    } finally {
        document.getElementById('btnCheckMember').disabled = false;
    }
}

function unlockNextSteps() {
    document.getElementById('btnMpmQr').disabled = false;
    document.getElementById('btnCpmQr').disabled = false;

    // Pay Status UUID 필드 자동 채움 (계정 선택 후 즉시 반영)
    document.getElementById('mpmStatusUuid').value = state.selectedUuid;
    onStatusFieldInput();
}

/* Pay Status 입력 필드 변경 시 버튼 활성화 여부 판단 */
function onStatusFieldInput() {
    const uuid   = document.getElementById('mpmStatusUuid').value.trim();
    const type   = document.getElementById('mpmStatusType').value.trim();
    const orgTno = document.getElementById('mpmStatusOrgTno').value.trim();
    document.getElementById('btnMpmStatus').disabled = !(uuid && type && orgTno);
}

/* =========================================
   MPM State
   ========================================= */
const mpmState = {
    lastQrVerifyTno: null,
    lastQrVerifyToken: null,
    lastVerifiedQrCode: null,
    lastMpmPaymentTno: null
};

function onMpmQrCodeInput() {
    // QR 코드 변경 시 이후 단계 상태 초기화
    resetMpmState();
}

function resetMpmQrCode() {
    document.getElementById('mpmQrCode').value = '';
    resetMpmState();
}

function resetMpmState() {
    mpmState.lastQrVerifyTno = null;
    mpmState.lastQrVerifyToken = null;
    mpmState.lastVerifiedQrCode = null;
    mpmState.lastMpmPaymentTno = null;
    document.getElementById('btnMpmPay').disabled = true;
    document.getElementById('btnMpmStatus').disabled = true;
    setBadge('badgeMpm', '대기', 'idle');
}

/* =========================================
   Step 4-1: MPM QR Verify
   ========================================= */
async function runMpmQr() {
    const qrCode = document.getElementById('mpmQrCode').value.trim();
    if (!qrCode) {
        appendError('Please enter the merchant QR code.');
        return;
    }
    if (!state.selectedUuid || !state.memberVerified) {
        appendError('Member check must be completed first.');
        return;
    }

    setBadge('badgeMpm', '검증 중...', 'run');
    document.getElementById('btnMpmQr').disabled = true;
    appendDivider();
    appendInfo(`Starting QR Verify — QR: ${qrCode}`);

    try {
        const response = await callApi('POST', '/api/payment/qr/verify', {
            uuid: state.selectedUuid,
            qrCode: qrCode
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/pay/qr/verify', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('UUID:', state.selectedUuid);
        appendKV('QR_CODE:', qrCode);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        appendKV('TNO:', result.tno);
        maybeLogErrorDetail(result);

        if (result.detail) {
            appendKV('RES_CD:', result.detail.RES_CD, result.detail.RES_CD === '0000' ? 'val-ok' : 'val-err');
            appendKV('RES_MSG:', result.detail.RES_MSG);
            if (result.detail.TOKEN) {
                appendKV('TOKEN:', result.detail.TOKEN);
            }
            if (result.detail.AFLT_INFO) {
                const a = result.detail.AFLT_INFO;
                appendKV('AFLT_ID:', a.AFLT_ID);
                appendKV('AFLT_NM:', a.AFLT_NM);
            }
        }

        if (result.rc === '0000' && result.detail?.RES_CD === '0000') {
            // 다음 단계 진행을 위한 상태 저장
            mpmState.lastQrVerifyTno   = result.tno;
            mpmState.lastQrVerifyToken = result.detail.TOKEN;
            mpmState.lastVerifiedQrCode = qrCode;

            setBadge('badgeMpm', 'QR 검증 완료', 'run');
            document.getElementById('btnMpmPay').disabled = false;
            appendInfo(`QR verify complete. TOKEN and TNO saved. MPM Pay button unlocked.`);
        } else {
            setBadge('badgeMpm', '실패', 'error');
            appendError(`QR verify failed — RC: ${result.rc}, RM: ${result.rm}`);
        }

    } catch (e) {
        setBadge('badgeMpm', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    } finally {
        document.getElementById('btnMpmQr').disabled = false;
    }
}

/* =========================================
   Step 4-2: MPM Payment
   ========================================= */
async function runMpmPay() {
    // 사전 검증: QR 검증 완료 여부
    if (!mpmState.lastQrVerifyTno || !mpmState.lastQrVerifyToken) {
        appendError('QR Verify must be completed first. ORG_TNO and TOKEN are required.');
        return;
    }

    const amt = document.getElementById('mpmAmt').value.trim();
    if (!amt || Number(amt) <= 0) {
        appendError('Please enter a valid payment amount.');
        return;
    }

    setBadge('badgeMpm', '결제 중...', 'run');
    document.getElementById('btnMpmPay').disabled = true;
    appendDivider();
    appendInfo(`Starting MPM Pay — ORG_TNO: ${mpmState.lastQrVerifyTno}`);

    try {
        const response = await callApi('POST', '/api/payment/mpm', {
            uuid:   state.selectedUuid,
            orgTno: mpmState.lastQrVerifyTno,    // QR 검증 요청의 COMM.TNO
            token:  mpmState.lastQrVerifyToken,   // QR 검증 응답의 TOKEN
            qrCode: mpmState.lastVerifiedQrCode,
            amt:    amt
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/pay/mpm', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('UUID:', state.selectedUuid);
        appendKV('ORG_TNO:', mpmState.lastQrVerifyTno);
        appendKV('TOKEN:', mpmState.lastQrVerifyToken);
        appendKV('QR_CODE:', mpmState.lastVerifiedQrCode);
        appendKV('PAY_TYPE:', 'P');
        appendKV('AMT:', amt);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        appendKV('TNO:', result.tno);
        maybeLogErrorDetail(result);

        if (result.detail) {
            appendKV('RES_CD:', result.detail.RES_CD, result.detail.RES_CD === '0000' ? 'val-ok' : 'val-err');
            appendKV('RES_MSG:', result.detail.RES_MSG);

            if (result.detail.PAY_INFO) {
                const p = result.detail.PAY_INFO;
                appendKV('TRX_DT:', p.TRX_DT);
                appendKV('TRX_TM:', p.TRX_TM);
                appendKV('TRX_SEQ:', p.TRX_SEQ);
                appendKV('AFLT_NM:', p.AFLT_NM);
                appendKV('AMT:', p.AMT);
            }
        }

        if (result.rc === '0000') {
            mpmState.lastMpmPaymentTno = result.tno;

            // Pay Status 입력 필드 자동 채움 (UUID + TYPE + ORG_TNO)
            document.getElementById('mpmStatusUuid').value   = state.selectedUuid;
            document.getElementById('mpmStatusType').value   = 'M';
            document.getElementById('mpmStatusOrgTno').value = result.tno;
            onStatusFieldInput();

            setBadge('badgeMpm', '결제 완료', 'done');
            document.getElementById('btnMpmStatus').disabled = false;
            appendInfo(`MPM payment complete. STATUS fields auto-filled. Pay Status button unlocked.`);
        } else {
            setBadge('badgeMpm', '실패', 'error');
            appendError(`MPM pay failed — RC: ${result.rc}, RM: ${result.rm}`);
            document.getElementById('btnMpmPay').disabled = false;
        }

    } catch (e) {
        setBadge('badgeMpm', '오류', 'error');
        appendError(`Request error: ${e.message}`);
        document.getElementById('btnMpmPay').disabled = false;
    }
}

/* =========================================
   Step 4-3: MPM Pay Status
   입력 필드 값 우선 사용, 순서 진행 시 자동 채워짐
   ========================================= */
async function runMpmStatus() {
    const uuid   = document.getElementById('mpmStatusUuid').value.trim();
    const type   = document.getElementById('mpmStatusType').value.trim();
    const orgTno = document.getElementById('mpmStatusOrgTno').value.trim();

    // 버튼 활성화 조건과 동일하지만 방어적 체크
    if (!uuid || !type || !orgTno) {
        appendError('UUID, TYPE, ORG_TNO are all required.');
        return;
    }

    document.getElementById('btnMpmStatus').disabled = true;
    appendDivider();
    appendInfo(`Starting Pay Status — UUID: ${uuid}, TYPE: ${type}, ORG_TNO: ${orgTno}`);

    try {
        const response = await callApi('POST', '/api/payment/status', {
            uuid,
            type,
            orgTno
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/pay/status', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('UUID:', uuid);
        appendKV('TYPE:', type);
        appendKV('ORG_TNO:', orgTno);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        maybeLogErrorDetail(result);

        if (result.detail) {
            const resCdLabel = resolvePayStatusLabel(result.detail.RES_CD);
            appendKV('RES_CD:', `${result.detail.RES_CD}  ${resCdLabel}`,
                result.detail.RES_CD === '0000' ? 'val-ok' : 'val-err');
            appendKV('RES_MSG:', result.detail.RES_MSG);

            if (result.detail.PAY_INFO) {
                const p = result.detail.PAY_INFO;
                appendKV('TRX_DT:', p.TRX_DT);
                appendKV('TRX_TM:', p.TRX_TM);
                appendKV('TRX_SEQ:', p.TRX_SEQ);
                appendKV('AFLT_NM:', p.AFLT_NM);
                appendKV('AMT:', p.AMT);
            }
        }

        // 결제 완료 여부에 무관하게 버튼 재활성화 (반복 조회 가능)
        onStatusFieldInput();
        appendInfo('Pay Status check complete.');

    } catch (e) {
        setBadge('badgeMpm', '오류', 'error');
        appendError(`Request error: ${e.message}`);
        onStatusFieldInput();
    }
}

function resolvePayStatusLabel(resCd) {
    const map = {
        '0000': '(결제완료)',
        '0001': '(결제실패)',
        '0002': '(결제대기중)',
        '0003': '(바코드만료)',
        '0004': '(결제진행중)'
    };
    return map[resCd] || '';
}

/* =========================================
   CPM State
   ========================================= */
const cpmState = {
    cpmQrCode: null,
    cpmCreateRequestTno: null  // QR 생성 요청 COMM.TNO → 결제상태조회 ORG_TNO (TYPE=C)
};

/* =========================================
   Step 5-1: CPM QR/BAR코드 생성
   ========================================= */
async function runCpmQr() {
    if (!state.selectedUuid || !state.memberVerified) {
        appendError('Member check must be completed first.');
        return;
    }

    setBadge('badgeCpm', '생성 중...', 'run');
    document.getElementById('btnCpmQr').disabled = true;
    appendDivider();
    appendInfo(`Starting CPM QR Create — UUID: ${state.selectedUuid}`);

    try {
        const response = await callApi('POST', '/api/payment/qr/create', {
            uuid: state.selectedUuid
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/pay/qr/create', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('UUID:', state.selectedUuid);
        appendKV('PAY_TYPE:', 'P');
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        appendKV('TNO:', result.tno);
        maybeLogErrorDetail(result);

        if (result.detail) {
            appendKV('QR_CODE:', result.detail.QR_CODE);
            appendKV('EXP_TIME:', result.detail.EXP_TIME);
            // BAR_CODE는 로그에만 표시, UI에는 렌더링하지 않음
            appendKV('BAR_CODE:', result.detail.BAR_CODE + '  (log only, not rendered)');
        }

        if (result.rc === '0000' && result.detail?.QR_CODE) {
            cpmState.cpmQrCode          = result.detail.QR_CODE;
            cpmState.cpmCreateRequestTno = result.tno;

            // QR 이미지 렌더링
            renderQrImage(result.detail.QR_CODE, result.detail.EXP_TIME);

            setBadge('badgeCpm', 'QR 준비', 'run');
            document.getElementById('btnCpmQr').disabled = false;
            document.getElementById('btnCpmStatus').disabled = false;
            appendInfo(`QR code rendered. EXP_TIME: ${result.detail.EXP_TIME} (170s). Pay Status unlocked.`);
        } else {
            setBadge('badgeCpm', '실패', 'error');
            appendError(`QR create failed — RC: ${result.rc}, RM: ${result.rm}`);
            document.getElementById('btnCpmQr').disabled = false;
        }

    } catch (e) {
        setBadge('badgeCpm', '오류', 'error');
        appendError(`Request error: ${e.message}`);
        document.getElementById('btnCpmQr').disabled = false;
    }
}

function renderQrImage(qrCode, expTime) {
    const wrap = document.getElementById('qrImageWrap');
    // 기존 QR 초기화
    wrap.innerHTML = '';

    new QRCode(wrap, {
        text: qrCode,
        width: 130,
        height: 130,
        colorDark: '#1a1a2e',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.M
    });

    document.getElementById('qrCodeText').textContent = qrCode;
    document.getElementById('qrExpTime').textContent  = formatExpTime(expTime);
    document.getElementById('qrDisplay').style.display = 'block';
}

function formatExpTime(expTime) {
    // yyyymmddhh24miss → yyyy-MM-dd HH:mm:ss
    if (!expTime || expTime.length !== 14) return expTime;
    return `${expTime.slice(0,4)}-${expTime.slice(4,6)}-${expTime.slice(6,8)} `
         + `${expTime.slice(8,10)}:${expTime.slice(10,12)}:${expTime.slice(12,14)}`;
}

/* =========================================
   Step 5-2: CPM Pay Status
   TYPE=C, ORG_TNO=QR 생성 요청 COMM.TNO
   ========================================= */
async function runCpmStatus() {
    if (!cpmState.cpmCreateRequestTno) {
        appendError('CPM QR must be created first. ORG_TNO (QR create TNO) is required.');
        return;
    }

    const uuid   = state.selectedUuid;
    const type   = 'C';
    const orgTno = cpmState.cpmCreateRequestTno;

    setBadge('badgeCpm', '조회 중...', 'run');
    document.getElementById('btnCpmStatus').disabled = true;
    appendDivider();
    appendInfo(`Starting CPM Pay Status — UUID: ${uuid}, TYPE: ${type}, ORG_TNO: ${orgTno}`);

    try {
        const response = await callApi('POST', '/api/payment/status', {
            uuid,
            type,
            orgTno
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/pay/status (CPM)', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('UUID:', uuid);
        appendKV('TYPE:', type + '  (C = CPM)');
        appendKV('ORG_TNO:', orgTno);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        maybeLogErrorDetail(result);

        if (result.detail) {
            const resCdLabel = resolvePayStatusLabel(result.detail.RES_CD);
            appendKV('RES_CD:', `${result.detail.RES_CD}  ${resCdLabel}`,
                result.detail.RES_CD === '0000' ? 'val-ok' : 'val-err');
            appendKV('RES_MSG:', result.detail.RES_MSG);

            if (result.detail.PAY_INFO) {
                const p = result.detail.PAY_INFO;
                appendKV('TRX_DT:', p.TRX_DT);
                appendKV('TRX_TM:', p.TRX_TM);
                appendKV('TRX_SEQ:', p.TRX_SEQ);
                appendKV('AFLT_NM:', p.AFLT_NM);
                appendKV('AMT:', p.AMT);
            }
        }

        if (result.rc === '0000' && result.detail?.RES_CD === '0000') {
            setBadge('badgeCpm', '완료', 'done');
        } else {
            setBadge('badgeCpm', 'QR 준비', 'run');
        }

        // 반복 조회 가능
        document.getElementById('btnCpmStatus').disabled = false;
        appendInfo('CPM Pay Status check complete.');

    } catch (e) {
        setBadge('badgeCpm', '오류', 'error');
        appendError(`Request error: ${e.message}`);
        document.getElementById('btnCpmStatus').disabled = false;
    }
}
/* =========================================
   Reconciliation: 결제내역 집계 조회
   ========================================= */
async function runSettleSummary() {
    const strDate = document.getElementById('strDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!strDate || !endDate) {
        appendError('Start Date and End Date are required.');
        return;
    }

    // date input returns YYYY-MM-DD, API needs YYYYMMDD
    const strDateFmt = strDate.replace(/-/g, '');
    const endDateFmt = endDate.replace(/-/g, '');

    setBadge('badgeSettle', '조회 중...', 'run');
    document.getElementById('btnSettleSummary').disabled = true;
    appendDivider();
    appendInfo(`Starting Settlement Summary — ${strDateFmt} ~ ${endDateFmt}`);

    try {
        const response = await callApi('POST', '/api/settle/summary', {
            strDate: strDateFmt,
            endDate: endDateFmt
        });
        const result = response.data;

        // --- Request ---
        appendSection('Request  ·  POST /bravo/v1/settle/summary', 'section-req');
        appendKV('API:', result.apiName);
        appendKV('STR_DATE:', strDateFmt);
        appendKV('END_DATE:', endDateFmt);
        appendJson('Plain JSON', result.requestPlainJson, 'req');
        appendEncrypted('Encrypted DATA', result.requestEncryptedData);

        // --- Response ---
        appendSection('Response', 'section-res');
        appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
        appendJson('Decrypted JSON', result.responsePlainJson, 'res');

        // --- Summary ---
        appendSection('Summary');
        appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
        appendKV('RM:', result.rm);
        maybeLogErrorDetail(result);

        if (result.detail) {
            appendKV('RES_CD:', result.detail.RES_CD, result.detail.RES_CD === '0000' ? 'val-ok' : 'val-err');
            appendKV('RES_MSG:', result.detail.RES_MSG);

            const bp = result.detail.BP_SUMMARY_INFO;
            if (bp) {
                appendKV('BP  TOT_PAY_CNT / AMT:', `${bp.TOT_PAY_CNT ?? '-'} 건 / ${bp.TOT_PAY_AMT ?? '-'} 원`);
                appendKV('BP  TOT_RFND_CNT / AMT:', `${bp.TOT_RFND_CNT ?? '-'} 건 / ${bp.TOT_RFND_AMT ?? '-'} 원`);
                appendKV('BP  TOT_CNCL_CNT / AMT:', `${bp.TOT_CNCL_CNT ?? '-'} 건 / ${bp.TOT_CNCL_AMT ?? '-'} 원`);
                appendKV('BP  TOT_RFND_CNCL_CNT / AMT:', `${bp.TOT_RFND_CNCL_CNT ?? '-'} 건 / ${bp.TOT_RFND_CNCL_AMT ?? '-'} 원`);
            }
        }

        if (result.rc === '0000') {
            setBadge('badgeSettle', '완료', 'done');
        } else {
            setBadge('badgeSettle', '실패', 'error');
            appendError(`Settlement summary failed — RC: ${result.rc}, RM: ${result.rm}`);
        }

        document.getElementById('btnSettleSummary').disabled = false;
        appendInfo('Settlement Summary complete.');

    } catch (e) {
        setBadge('badgeSettle', '오류', 'error');
        appendError(`Request error: ${e.message}`);
        document.getElementById('btnSettleSummary').disabled = false;
    }
}

/* =========================================
   Panel Toggle — 순방향 ↔ 역거래
   ========================================= */
function toggleReverseMode(isReverse) {
    document.getElementById('panelMain').style.display    = isReverse ? 'none'  : 'block';
    document.getElementById('panelReverse').style.display = isReverse ? 'block' : 'none';

    // 레이블 활성화 스타일
    document.getElementById('labelMain').classList.toggle('is-active', !isReverse);
    document.getElementById('labelReverse').classList.toggle('is-active', isReverse);

    // 로그 초기화
    clearLog();

    // 패널 입력값 초기화
    if (isReverse) {
        resetReversePanel();
    } else {
        resetMainPanel();
    }
    appendInfo(isReverse
        ? '역거래 API 테스트 모드로 전환되었습니다.'
        : '시나리오 API 테스트 모드로 전환되었습니다.');
}

function resetReversePanel() {
    // 모든 text input 초기화 (select는 첫 옵션 유지)
    document.querySelectorAll('#panelReverse input[type="text"]').forEach(el => {
        el.value = '';
    });
    // select 초기화
    ['r2_type', 'r3_type', 'r4_piProcSt'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.selectedIndex = 0;
    });
    // UUID 기본값 (선택된 계정)
    const uuid = state.selectedUuid;
    ['r1_uuid', 'r2_uuid', 'r3_uuid', 'r4_uuid'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = uuid;
    });
    // CPM 완료 통지 SUPY_AMT, VAT, SVC_AMT 기본값
    ['r4_piSupyAmt', 'r4_piVat', 'r4_piSvcAmt'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '0';
    });
    // 배지 초기화
    ['badgeRefundNoti', 'badgeTranResult', 'badgePayRequest', 'badgeCpmComplete']
        .forEach(id => setBadge(id, '대기', 'idle'));
}

function resetMainPanel() {
    // MPM 상태 초기화
    resetMpmState();
    document.getElementById('mpmAmt').value = '';
    document.getElementById('mpmStatusUuid').value = '';
    document.getElementById('mpmStatusOrgTno').value = '';
    onStatusFieldInput();

    // CPM 상태 초기화
    cpmState.cpmQrCode = null;
    cpmState.cpmCreateRequestTno = null;
    document.getElementById('qrDisplay').style.display = 'none';
    document.getElementById('qrImageWrap').innerHTML = '';
    document.getElementById('btnCpmStatus').disabled = true;

    // 계정 배지만 초기화 (계정 선택은 유지)
    state.memberVerified = false;
    setBadge('badgeAccount', '대기', 'idle');
    setBadge('badgeMpm', '대기', 'idle');
    setBadge('badgeCpm', '대기', 'idle');
    setBadge('badgeSettle', '대기', 'idle');

    // 단계 버튼 비활성화
    document.getElementById('btnMpmQr').disabled    = true;
    document.getElementById('btnCpmQr').disabled    = true;
    document.getElementById('btnMpmStatus').disabled = true;
}

/* =========================================
   역거래 API — 공통 로그 출력
   ========================================= */
function logReverseResult(result) {
    appendSection(`요청  ·  ${result.url}`, 'section-req');
    appendKV('API:', result.apiName);
    appendJson('Plain JSON', result.requestPlainJson, 'req');
    appendEncrypted('Encrypted DATA', result.requestEncryptedData);

    appendSection('응답', 'section-res');
    appendEncrypted('Encrypted DATA (received)', result.responseEncryptedData);
    appendJson('Decrypted JSON', result.responsePlainJson, 'res');

    appendSection('Summary');
    appendKV('RC:', result.rc, result.rc === '0000' ? 'val-ok' : 'val-err');
    appendKV('RM:', result.rm);
    if (result.tno) appendKV('TNO:', result.tno);
    maybeLogErrorDetail(result);
    if (result.detail) {
        const d = result.detail;
        if (d.RES_CD) appendKV('RES_CD:', d.RES_CD, d.RES_CD === '0000' ? 'val-ok' : 'val-err');
        if (d.RES_MSG) appendKV('RES_MSG:', d.RES_MSG);
        if (d.AMT)     appendKV('AMT:', d.AMT);
        if (d.BALANCE) appendKV('BALANCE:', d.BALANCE);
    }
}

/* =========================================
   역거래 1: 환불결과 통지
   ========================================= */
async function runRefundNoti() {
    const body = {
        uuid:        document.getElementById('r1_uuid').value.trim(),
        trgtTrxDt:   document.getElementById('r1_trgtTrxDt').value.trim(),
        trgtTrxSeq:  document.getElementById('r1_trgtTrxSeq').value.trim(),
        orgTrxDt:    document.getElementById('r1_orgTrxDt').value.trim(),
        orgTrxSeq:   document.getElementById('r1_orgTrxSeq').value.trim(),
        amt:         document.getElementById('r1_amt').value.trim(),
    };
    if (!body.uuid || !body.trgtTrxDt || !body.trgtTrxSeq || !body.amt) {
        appendError('UUID, TRGT_TRX_DT, TRGT_TRX_SEQ, AMT는 필수 입력값입니다.');
        return;
    }
    setBadge('badgeRefundNoti', '처리 중...', 'run');
    appendDivider();
    appendInfo('환불결과 통지 처리 시작');
    try {
        const response = await callApi('POST', '/api/test/reverse/refund-noti', body);
        logReverseResult(response.data);
        setBadge('badgeRefundNoti', response.data.rc === '0000' ? '완료' : '실패',
                 response.data.rc === '0000' ? 'done' : 'error');
        appendInfo('환불결과 통지 처리 완료');
    } catch (e) {
        setBadge('badgeRefundNoti', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    }
}

/* =========================================
   역거래 2: 포인트 거래결과 조회
   ========================================= */
async function runTranResult() {
    const body = {
        uuid:       document.getElementById('r2_uuid').value.trim(),
        type:       document.getElementById('r2_type').value,
        trgtTrxDt:  document.getElementById('r2_trgtTrxDt').value.trim(),
        trgtTrxSeq: document.getElementById('r2_trgtTrxSeq').value.trim(),
        orgTrxDt:   document.getElementById('r2_orgTrxDt').value.trim(),
        orgTrxSeq:  document.getElementById('r2_orgTrxSeq').value.trim(),
    };
    if (!body.uuid || !body.trgtTrxDt || !body.trgtTrxSeq) {
        appendError('UUID, TRGT_TRX_DT, TRGT_TRX_SEQ는 필수 입력값입니다.');
        return;
    }
    setBadge('badgeTranResult', '처리 중...', 'run');
    appendDivider();
    appendInfo(`포인트 거래결과 조회 시작 — TYPE: ${body.type}`);
    try {
        const response = await callApi('POST', '/api/test/reverse/tran-result', body);
        logReverseResult(response.data);
        setBadge('badgeTranResult', response.data.rc === '0000' ? '완료' : '실패',
                 response.data.rc === '0000' ? 'done' : 'error');
        appendInfo('포인트 거래결과 조회 처리 완료');
    } catch (e) {
        setBadge('badgeTranResult', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    }
}

/* =========================================
   역거래 3: 포인트 차감/환불/망취소 요청
   ========================================= */
async function runPayRequest() {
    const body = {
        uuid:      document.getElementById('r3_uuid').value.trim(),
        type:      document.getElementById('r3_type').value,
        trxDt:     document.getElementById('r3_trxDt').value.trim(),
        trxSeq:    document.getElementById('r3_trxSeq').value.trim(),
        amt:       document.getElementById('r3_amt').value.trim(),
        afltId:    document.getElementById('r3_afltId').value.trim(),
        afltNm:    document.getElementById('r3_afltNm').value.trim(),
        mpmTno:    document.getElementById('r3_mpmTno').value.trim(),
        orgTrxDt:  document.getElementById('r3_orgTrxDt').value.trim(),
        orgTrxSeq: document.getElementById('r3_orgTrxSeq').value.trim(),
    };
    if (!body.uuid || !body.trxDt || !body.trxSeq || !body.amt || !body.afltId || !body.afltNm) {
        appendError('UUID, TRX_DT, TRX_SEQ, AMT, AFLT_ID, AFLT_NM은 필수 입력값입니다.');
        return;
    }
    setBadge('badgePayRequest', '처리 중...', 'run');
    appendDivider();
    appendInfo(`포인트 요청 처리 시작 — TYPE: ${body.type}`);
    try {
        const response = await callApi('POST', '/api/test/reverse/pay-request', body);
        logReverseResult(response.data);
        setBadge('badgePayRequest', response.data.rc === '0000' ? '완료' : '실패',
                 response.data.rc === '0000' ? 'done' : 'error');
        appendInfo('포인트 요청 처리 완료');
    } catch (e) {
        setBadge('badgePayRequest', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    }
}

/* =========================================
   역거래 4: CPM 결제완료 통지
   ========================================= */
async function runCpmComplete() {
    const body = {
        uuid:        document.getElementById('r4_uuid').value.trim(),
        orgTno:      document.getElementById('r4_orgTno').value.trim(),
        piTrxDt:     document.getElementById('r4_piTrxDt').value.trim(),
        piTrxTm:     document.getElementById('r4_piTrxTm').value.trim(),
        piTrxSeq:    document.getElementById('r4_piTrxSeq').value.trim(),
        piProcSt:    document.getElementById('r4_piProcSt').value,
        piAfltId:    document.getElementById('r4_piAfltId').value.trim(),
        piAfltNm:    document.getElementById('r4_piAfltNm').value.trim(),
        piAmt:       document.getElementById('r4_piAmt').value.trim(),
        piSupyAmt:   document.getElementById('r4_piSupyAmt').value.trim(),
        piVat:       document.getElementById('r4_piVat').value.trim(),
        piSvcAmt:    document.getElementById('r4_piSvcAmt').value.trim(),
        piBizNo:     document.getElementById('r4_piBizNo').value.trim(),
        piUpjongNm:  document.getElementById('r4_piUpjongNm').value.trim(),
        piReprNm:    document.getElementById('r4_piReprNm').value.trim(),
        piTelNo:     document.getElementById('r4_piTelNo').value.trim(),
        piAddr:      document.getElementById('r4_piAddr').value.trim(),
        piAddrDtl:   document.getElementById('r4_piAddrDtl').value.trim(),
    };
    if (!body.uuid || !body.orgTno || !body.piTrxDt || !body.piTrxSeq || !body.piAmt) {
        appendError('UUID, ORG_TNO, PAY_INFO.TRX_DT, TRX_SEQ, AMT는 필수 입력값입니다.');
        return;
    }
    setBadge('badgeCpmComplete', '처리 중...', 'run');
    appendDivider();
    appendInfo(`CPM 결제완료 통지 처리 시작 — PROC_ST: ${body.piProcSt}`);
    try {
        const response = await callApi('POST', '/api/test/reverse/cpm-complete', body);
        logReverseResult(response.data);
        setBadge('badgeCpmComplete', response.data.rc === '0000' ? '완료' : '실패',
                 response.data.rc === '0000' ? 'done' : 'error');
        appendInfo('CPM 결제완료 통지 처리 완료');
    } catch (e) {
        setBadge('badgeCpmComplete', '오류', 'error');
        appendError(`Request error: ${e.message}`);
    }
}

/* =========================================
   환경 토글 — 개발 ↔ 운영
   ========================================= */
async function switchEnv(isProd) {
    const env = isProd ? 'prod' : 'dev';
    try {
        const response = await callApi('POST', '/api/env', { env });
        const d = response.data;
        document.getElementById('envLabelDev').classList.toggle('env-label-active', !isProd);
        document.getElementById('envLabelProd').classList.toggle('env-label-active', isProd);
        appendDivider();
        appendInfo(`환경 전환 → ${d.label}  (${d.baseUrl})`);
    } catch (e) {
        appendError(`환경 전환 실패: ${e.message}`);
        // 실패 시 토글 원상복구
        document.getElementById('toggleEnv').checked = !isProd;
    }
}

/* =========================================
   Toolbar
   ========================================= */
function clearLog() {
    document.getElementById('logPanel').innerHTML = '';
    logCount = 0;
    document.getElementById('logCount').textContent = '0 건';
    appendInfo('Log cleared.');
}

function copyLog() {
    const text = document.getElementById('logPanel').innerText;
    navigator.clipboard.writeText(text)
        .then(() => appendInfo('Log copied to clipboard.'))
        .catch(() => appendError('Failed to copy log to clipboard.'));
}
