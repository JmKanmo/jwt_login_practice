class SignupController extends UtilController {
    constructor() {
        super();
        this.signupForm = document.getElementById("signupForm");
        this.signupFormButton = document.getElementById("signup_form_button");
    }

    initSignupController() {
        this.signupFormButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            xhr.open("POST", "/member/signup");

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = JSON.parse(event.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue["message"]);
                } else {
                    this.showToastMessage("회원가입에 성공했습니다.");
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('회원가입에 실패하였습니다.');
            });

            xhr.send(new FormData(this.signupForm));
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const signupController = new SignupController();
    signupController.initSignupController();
});