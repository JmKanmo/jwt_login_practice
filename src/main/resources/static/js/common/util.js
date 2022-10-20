class UtilController {
    showToastMessage(message) {
        Toastify({
            text: message,
            duration: 3000,
            close: true,
            position: "center",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            }
        }).showToast();
    }

    showToastMessage(message, isClose, duration, dismissListener) {
        Toastify({
            text: message,
            duration: duration,
            close: isClose,
            position: "center",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            },
            callback: dismissListener
        }).showToast();
    }

    setLocalStorage(key, value) {
        localStorage.setItem(key, value);
    }

    getLocalStorage(key) {
        return localStorage.getItem(key);
    }

    sendAuthorize() {
        return new Promise((resolve, reject) => {
            const authorizeXhr = new XMLHttpRequest();
            authorizeXhr.open("POST", "/member/authorize", true);
            authorizeXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

            authorizeXhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = JSON.parse(event.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    // AccessToken 만료 or 토큰 입력 정보가 올바르지 않아서 or 미인증 접속 으로 인해 Filter/Controller 측에서 걸러짐
                    resolve(false);
                } else {
                    this.sendReissue(responseValue);
                    resolve(true);
                }
            });

            authorizeXhr.addEventListener("error", event => {
                this.showToastMessage('인증에 실패하였습니다.');
            });

            authorizeXhr.send();
        });
    }

    sendReissue(responseValue) {
        const reissueXhr = new XMLHttpRequest();
        reissueXhr.open("POST", "/member/reissue", true);
        reissueXhr.setRequestHeader("Authorization", responseValue["accessToken"]);

        reissueXhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) === false) {
                this.setLocalStorage("Authorization", responseValue["grantType"] + responseValue["accessToken"]);
            }
        });

        reissueXhr.addEventListener("error", event => {
            this.showToastMessage('토큰 재발급에 실패하였습니다.');
        });

        reissueXhr.send();
    }
}