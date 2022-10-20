class TradeController extends UtilController {
    constructor() {
        super();
        this.userProfileContainer = document.getElementById("user_profile_container");
        this.userProfileInfoName = document.getElementById("user_info_name");
        this.userProfileInfoIntro = document.getElementById("user_info_intro");
        this.loginLink = document.getElementById("login_link");
        this.tradeResultInquireButton = document.getElementById("trade_result_inquire_button");
        this.tradeIndicators = document.getElementById("trade_indicators");
        this.tradeRequestText = document.getElementById("trade_request_text");
    }

    initTradeController() {
        this.initEventListener();

        this.sendAuthorize().then(ret => {
            if (ret === true) {
                // main api 호출 및 데이터 화면 표시
                this.#sendUserData();
            } else {
                // 경우에 따라, 로그인 요청 팝업 띄우거나, 페이지로 넘어가거나 등등 여러 조치를 취할 수 있음.
            }
        });

        this.#inquireTradeData();
    }

    initEventListener() {
        this.tradeResultInquireButton.addEventListener("click", evt => {
            const tradeXhr = new XMLHttpRequest();
            tradeXhr.open("POST", "/global/trade/request");
            tradeXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

            tradeXhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if (status === 200) {
                    this.tradeRequestText.textContent = responseValue;
                } else {
                    this.showToastMessage("로그인 되어있지 않습니다.");
                }
            });

            tradeXhr.addEventListener("error", event => {
                this.showToastMessage("거래 정보를 불러오지 못했습니다.");
            });

            tradeXhr.send();
        });
    }

    #sendUserData() {
        const mainXhr = new XMLHttpRequest();
        mainXhr.open("GET", "/member/info");
        mainXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

        mainXhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (status === 200) {
                this.loginLink.style.display = 'none';
                this.userProfileContainer.style.display = 'block';
                this.userProfileInfoName.textContent = responseValue["username"];
                this.userProfileInfoIntro.textContent = responseValue["intro"];
            } else {
                this.userProfileContainer.style.display = 'none';
                this.loginLink.style.display = 'block';
                this.userProfileInfoName.textContent = ``;
                this.userProfileInfoName.textContent = ``;
            }
        });

        mainXhr.addEventListener("error", event => {
            this.showToastMessage("사용자 정보를 불러오지 못했습니다.");
        });

        mainXhr.send();
    }

    #inquireTradeData() {
        const tradeXhr = new XMLHttpRequest();
        tradeXhr.open("GET", "/global/trade/inquire");
        tradeXhr.setRequestHeader("Authorization", this.getLocalStorage("Authorization"));

        tradeXhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = event.target.responseText;

            if (status === 200) {
                this.tradeIndicators.textContent = responseValue;
            }
        });

        tradeXhr.addEventListener("error", event => {
            this.showToastMessage("거래 정보를 불러오지 못했습니다.");
        });

        tradeXhr.send();
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const tradeController = new TradeController();
    tradeController.initTradeController();
});