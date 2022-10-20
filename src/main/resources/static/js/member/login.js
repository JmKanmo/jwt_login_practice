class LoginController extends UtilController {
    constructor() {
        super();
        this.loginForm = document.getElementById("loginForm");
        this.loginButton = document.getElementById("login_form_button");
    }

    initLoginController() {
        this.loginButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            xhr.open("POST", "/member/login");

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = JSON.parse(event.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue["message"]);
                } else {
                    this.setLocalStorage("Authorization", responseValue["grantType"] + responseValue["accessToken"]);
                    window.location = document.referrer;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('로그인에 실패하였습니다.');
            });

            xhr.send(new FormData(this.loginForm));
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const loginController = new LoginController();
    loginController.initLoginController();
});