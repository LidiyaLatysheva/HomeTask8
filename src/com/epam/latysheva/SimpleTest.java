package com.epam.latysheva;


import com.epam.latysheva.page.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by Lidziya_Latyshava on 6/22/2017.
 */
public class SimpleTest {
    private WebDriver driver;
    WebDriverWait wait;

    //Data
    private static final String PATH_TO_GEKODRIVER = "lib/geckodriver.exe";
    private static final String GECKO_DRIVER_SYSTEM_PROPERTY = "webdriver.gecko.driver";
    protected static final String LOGIN = "lida.test.2017";
    protected static final String PASSWORD = "$ERDFC5rtfgv";
    protected static final String EMAIL_DETAILS_TO = "lida.test.2017@mail.ru";
    protected static final String EMAIL_DETAILS_SUBJ = "TEST email";
    protected static final String EMAIL_DETAILS_BODY = "Hello, dear!";

    protected static final String CHECK_BODY_IS_THE_SAME_MSG = "FAIL: Body is different";
    protected static final String CHECK_EMAIL_IS_SENT_MSG = "FAIL: Email is not sent";
    protected static final String CHECK_EMAIL_IS_SAVED_MSG = "FAIL: Email is not saved";
    protected static final String CHECK_EMAIL_IS_IN_DRAFTS_MSG = "FAIL: Check that email appears in Drafts failed";
    protected static final String CHECK_EMAIL_IS_NOT_IN_DRAFTS_MSG = "FAIL: Check that email disappears from Drafts failed";
    protected static final String CHECK_EMAIL_IS_IN_SENTS_MSG = "FAIL: Check that email appears in Sents failed";
    protected static final String CHECK_SEND_BTN_PRESENT_FAILED_MSG = "FAIL: Check if Send button is on the page failed";
    protected static final String CHECK_LOGOUT_MSG = "FAIL: Logout was unsuccessful";
    protected static final String CHECK_EMAIL_COUNT_INCREASE_MSG = "FAIL: Email count hasn't been increased by 1";
    protected static final String EMAIL_COUNT_AFTER_REFRESH_MSG = "FAIL: Email count is different after refresh";

    @BeforeTest
    private void initDriver() {
        /**
         * Set System variable webdriver.gecko.driver.
         */
        System.setProperty(GECKO_DRIVER_SYSTEM_PROPERTY, PATH_TO_GEKODRIVER);
        /**
         * Initialize webdriver.
         * Set pageLoadTimeout and delete all cookies.
         */
        driver = new FirefoxDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);
    }

    @Test
    public void firstTest() {
        /**
         * Open home page and login
         */
        HomePage homePage = new HomePage(driver);
        InboxPage inboxPage = homePage.open().login(LOGIN, PASSWORD);
        /**
         * Wait for Compose button to be clickable and click it.
         */
        inboxPage.waitForElementEnabled(inboxPage.getCOMPOSE_BUTTON());
        ComposePage composePage = inboxPage.clickComposeBtn();
        Assert.assertTrue(composePage.isComposePage(), CHECK_SEND_BTN_PRESENT_FAILED_MSG);
        /**
         * Fill in  To, Subject, Body fields,
         * save email as draft.
         * Verify that email saving message appears.
         */
        composePage.fillToField(EMAIL_DETAILS_TO).fillSubjectField(EMAIL_DETAILS_SUBJ).fillBodyField(EMAIL_DETAILS_BODY);
        composePage.saveAsDraft();
        Assert.assertTrue(composePage.isSavedToDrafts(), CHECK_EMAIL_IS_SAVED_MSG);
        /**
         *Go to Draft folder and check
         * that email appears there
         */
        DraftsPage draftsPage = composePage.openDrafts();
        Assert.assertTrue(draftsPage.isEmailThere(), CHECK_EMAIL_IS_IN_DRAFTS_MSG);
        /**
         * Open saved email and check its body
         */
        composePage = draftsPage.openSavedEmail();
        Assert.assertTrue(composePage.isBodyTheSame(), CHECK_BODY_IS_THE_SAME_MSG);
        /**
         * Send email
         * Check it dissappears from Drafts
         * Check it appears in Setns
         */
        MailBoxPage mailBoxPage = composePage.clickSend();
        Assert.assertTrue(mailBoxPage.isEmailSent(), CHECK_EMAIL_IS_SENT_MSG);
        mailBoxPage.openDrafts();
        Boolean tmp = draftsPage.isEmailThere();
        Assert.assertFalse(draftsPage.isEmailThere(), CHECK_EMAIL_IS_NOT_IN_DRAFTS_MSG);
        SentsPage sentsPage = draftsPage.openSents();
        tmp = sentsPage.isEmailThere();
        Assert.assertTrue(tmp, CHECK_EMAIL_IS_IN_SENTS_MSG);
        /**
         * Logout and check that logout is successful
         */
        homePage = sentsPage.logout();
        Assert.assertTrue(homePage.isHomePage(), CHECK_LOGOUT_MSG);
    }

    @Test
    public void secondTest() {
        /**
         * Open home page and login
         */
        HomePage homePage = new HomePage(driver);
        InboxPage inboxPage = homePage.open().login(LOGIN, PASSWORD);
        /**
         * Wait for Compose button to be clickable and click it.
         */
        inboxPage.waitForElementEnabled(inboxPage.getCOMPOSE_BUTTON());
        inboxPage.setInitialEmailCount();
        ComposePage composePage = inboxPage.clickComposeBtn();
        Assert.assertTrue(composePage.isComposePage(), CHECK_SEND_BTN_PRESENT_FAILED_MSG);
        /**
         * Fill in  To, Subject, Body fields,
         * send email.
         */
        composePage.fillToField(EMAIL_DETAILS_TO).fillSubjectField(EMAIL_DETAILS_SUBJ).fillBodyField(EMAIL_DETAILS_BODY);
        MailBoxPage mailBoxPage = composePage.clickSend();
        composePage.waitEmailSent();
        Assert.assertTrue(mailBoxPage.isEmailSent(), CHECK_EMAIL_IS_SENT_MSG);
        //TODO implement this logic. Currently scipping.
        /**
         * Check that email count is increased by 1
         */
        /*inboxPage = mailBoxPage.openInbox();
        List<WebElement> list = driver.findElements(By.xpath("/*//*[@class=\"js-href b-datalist__item__link\"]"));
        int newEmailCount = inboxPage.setEmailCount();
        list = driver.findElements(By.xpath("/*//*[@class=\"js-href b-datalist__item__link\"]"));
        int e=inboxPage.getInitialEmailCount();
        Assert.assertEquals(newEmailCount - inboxPage.getInitialEmailCount(), 1, CHECK_EMAIL_COUNT_INCREASE_MSG);*/
        /**
         * Check that email is deleted
         */
        inboxPage = mailBoxPage.openInbox();
        inboxPage.selectRecievedEmail();
        inboxPage.clickDelete();
        //TODO how to catch js elements
        /*boolean tmp = inboxPage.isEmailDeleted();
        Assert.assertTrue(tmp);*/
        //TODO implement this logic. Currently scipping.
        /**
         * Refresh the page and check that email count hasn't been changed
         */
        /*int count = inboxPage.setEmailCount();
        driver.navigate().refresh();
        newEmailCount = inboxPage.setEmailCount();
        Assert.assertEquals(newEmailCount, count, EMAIL_COUNT_AFTER_REFRESH_MSG);*/
        driver.navigate().refresh();
        /**
         * Open Trash and check that email there
         */
        TrashPage trashPage = inboxPage.openTrash();
        Assert.assertTrue(trashPage.isEmailThere());
        /**
         * Logout
         */
        homePage = trashPage.logout();
        Assert.assertTrue(homePage.isHomePage(), CHECK_LOGOUT_MSG);
    }

    @Test
    public void thirdTest() {
        /**
         * Open home page and login
         */
        HomePage homePage = new HomePage(driver);
        InboxPage inboxPage = homePage.open().login(LOGIN, PASSWORD);
        /**
         * Wait for Compose button to be clickable and click it.
         */
        inboxPage.waitForElementEnabled(inboxPage.getCOMPOSE_BUTTON());
        inboxPage.setInitialEmailCount();
        ComposePage composePage = inboxPage.clickComposeBtn();
        Assert.assertTrue(composePage.isComposePage(), CHECK_SEND_BTN_PRESENT_FAILED_MSG);
        /**
         * Fill in  To, Subject, Body fields,
         * send email.
         */
        composePage.fillToField(EMAIL_DETAILS_TO).fillSubjectField(EMAIL_DETAILS_SUBJ).fillBodyField(EMAIL_DETAILS_BODY);
        MailBoxPage mailBoxPage = composePage.clickSend();
        composePage.waitEmailSent();
        Assert.assertTrue(mailBoxPage.isEmailSent(), CHECK_EMAIL_IS_SENT_MSG);
        //TODO "Check that email count is increased by 1" implement this logic. Currently scipping.

        /**
         * Move email to trash and check it
         */
        inboxPage = mailBoxPage.openInbox();
        inboxPage.selectRecievedEmail();
        inboxPage.clickMoveToTrash();
        //TODO check that confirm message appears:how to catch js elements
        /**
         * Refresh the page and check that email count hasn't been changed
         */
        //TODO check that email count is the same after refresh : implement this logic. Currently scipping.
        driver.navigate().refresh();
        /**
         * Open Trash and check that email there
         */
        TrashPage trashPage = inboxPage.openTrash();
        Assert.assertTrue(trashPage.isEmailThere());
        /**
         * Logout
         */
        homePage = trashPage.logout();
        Assert.assertTrue(homePage.isHomePage(), CHECK_LOGOUT_MSG);
    }

    @AfterTest
    private void closeDeriver() {
        //driver.close();
        driver.quit();
    }
}
