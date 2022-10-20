class TestController extends UtilController {
    constructor() {
        super();
        this.loginLink = document.getElementById("login_link");
        this.userInfoContainer = document.getElementById("user_info_container");
        this.userInfoName = document.getElementById("user_info_name");
        this.userInfoIntro = document.getElementById("user_info_intro");
        this.logoutButton = document.getElementById("logout_button");
    }

    initTestController() {
        this.sendAuthorize().then(ret => {
            if (ret === true) {
                // main api 호출 및 데이터 화면 표시
                this.#sendMainData();
            } else {
                // 경우에 따라, 로그인 요청 팝업 띄우거나, 페이지로 넘어가거나 등등 여러 조치를 취할 수 있음.
            }
        });

        this.logoutButton.addEventListener("click", evt => {
            if (confirm("로그아웃 하시겠습니까?")) {
                const logoutXhr = new XMLHttpRequest();
                logoutXhr.open("POST", "/member/logout");
                logoutXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

                logoutXhr.addEventListener("loadend", event => {
                    let status = event.target.status;

                    if (status === 200) {
                        this.showToastMessage("로그아웃 되었습니다.");
                        this.logoutButton.style.display = 'none';
                        this.loginLink.style.display = 'block';
                        this.userInfoIntro.textContent = ``;
                        this.userInfoName.textContent = ``;
                        this.userInfoContainer.style.display = 'none';
                    }
                });

                logoutXhr.addEventListener("error", event => {
                    this.showToastMessage('로그아웃에 실패하였습니다.');
                });

                logoutXhr.send();
            }
        });
    }

    #sendMainData() {
        const mainXhr = new XMLHttpRequest();
        mainXhr.open("GET", "/member/info");
        mainXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

        mainXhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (status === 200) {
                this.loginLink.style.display = 'none';
                this.userInfoContainer.style.display = 'block';
                this.userInfoName.textContent = responseValue["username"];
                this.userInfoIntro.textContent = responseValue["intro"];
                this.logoutButton.style.display = 'block';
            }
        });

        mainXhr.addEventListener("error", event => {
            this.showToastMessage('로그인에 실패하였습니다.');
        });

        mainXhr.send();
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const testController = new TestController();
    testController.initTestController();
});