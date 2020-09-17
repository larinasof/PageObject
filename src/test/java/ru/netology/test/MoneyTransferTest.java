package ru.netology.test;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyFromCard2ToCard1() {
        String sum = "1000";
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val cardsInfoPage = verificationPage.validVerify(verificationCode);
        int card1Balance = cardsInfoPage.getCard1Balance();
        int card2Balance = cardsInfoPage.getCard2Balance();
        val cardTransferPage = cardsInfoPage.card1Replenish();
        val fromCard2 = DataHelper.getCardInfoFromCard2();
        cardTransferPage.transferCardToCard(fromCard2, sum);
        assertEquals(card1Balance + Integer.parseInt(sum), cardsInfoPage.getCard1Balance());
        assertEquals(card2Balance - Integer.parseInt(sum), cardsInfoPage.getCard2Balance());
    }

    @Test
    void shouldTransferMoneyFromCard1ToCard2() {
        String sum = "1000";
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val cardsInfoPage = verificationPage.validVerify(verificationCode);
        int card1Balance = cardsInfoPage.getCard1Balance();
        int card2Balance = cardsInfoPage.getCard2Balance();
        val cardTransferPage = cardsInfoPage.card2Replenish();
        val fromCard1 = DataHelper.getCardInfoFromCard1();
        cardTransferPage.transferCardToCard(fromCard1, sum);
        assertEquals(card1Balance - Integer.parseInt(sum), cardsInfoPage.getCard1Balance());
        assertEquals(card2Balance + Integer.parseInt(sum), cardsInfoPage.getCard2Balance());
    }

    @Test
    void shouldNotTransferIfOverBalance() {
        String sum = "100_000";
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val cardsInfoPage = verificationPage.validVerify(verificationCode);
        val cardTransferPage = cardsInfoPage.card1Replenish();
        val fromCard2 = DataHelper.getCardInfoFromCard2();
        cardTransferPage.transferCardToCard(fromCard2, sum);
        assertEquals("Недостаточно средств на карте", cardTransferPage.errorNotification());
    }

    @Test
    void shouldNotTransferIfIncorrectCardNumber() {
        String sum = "1000";
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val cardsInfoPage = verificationPage.validVerify(verificationCode);
        val cardTransferPage = cardsInfoPage.card1Replenish();
        val fromCard3 = DataHelper.getCardInfoFromCard3();
        cardTransferPage.transferCardToCard(fromCard3, sum);
        assertEquals("Ошибка! Произошла ошибка", cardTransferPage.errorNotification());
    }
}
