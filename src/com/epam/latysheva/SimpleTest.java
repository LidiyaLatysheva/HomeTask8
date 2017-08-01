package com.epam.latysheva;


import com.epam.latysheva.page.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
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

    /**
     * Scenario 2:
     1. Login to the mailbox
     2. Verify that login is successful
     3. Create a new mail (fill addressee, subject and body fields (addressee should be yourself))
     4. Send email
     5. Click on Inbox (Входящие)
     6. Check that quantity of emails has increased by one
     7. Click Delete (Удалить) button
     8. Check that message "Удалено 1 письмо. Отменить" has appeared
     9. Refresh page
     10. Check that amount of emails is same as initially
     11. Click on Trash (Корзина)
     12. Check that deleted email is actually in Trash
     13. Logout
     */
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
        /**
         * Check that email count is increased by 1
         */
        inboxPage = mailBoxPage.openInbox();
        int newEmailCount = inboxPage.setEmailCount();
        Assert.assertEquals(newEmailCount - inboxPage.getInitialEmailCount(), 1, CHECK_EMAIL_COUNT_INCREASE_MSG);
        /**
         * Check that email is deleted
         */
        inboxPage = mailBoxPage.openInbox();
        inboxPage.selectRecievedEmail();
        inboxPage.clickDelete();
        Assert.assertTrue(inboxPage.isEmailDeletedMoved());
        /**
         * Refresh the page and check that email count hasn't been changed
         */
        int emailCount = inboxPage.setEmailCount();
        driver.navigate().refresh();
        newEmailCount = inboxPage.setEmailCount();
        Assert.assertEquals(newEmailCount, emailCount, EMAIL_COUNT_AFTER_REFRESH_MSG);
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

    /**
     * Scenario 3:
     1. Login to the mailbox
     2. Verify that login is successful
     3. Create a new mail (fill addressee, subject and body fields (addressee should be yourself))
     4. Send email
     5. Click on Inbox (Входящие)
     6. Check that quantity of emails is what was previously plus one
     7. Select email that you just sent in Inbox
     8. Click Move (Переместить) button
     9. Select "Trash" (Корзина) option
     10. Check that message "Письмо перемещено в Корзину. Отменить" has appeared
     11. Check that amount of emails is same as initially
     12. Refresh page
     13. Check that amount of emails is same as initially
     14. Click on Trash (Корзина)
     15. Check that trashed email is actually in Trash
     16. Logout
     */
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
        inboxPage = mailBoxPage.openInbox();
        int newEmailCount = inboxPage.setEmailCount();
        Assert.assertEquals(newEmailCount - inboxPage.getInitialEmailCount(), 1, CHECK_EMAIL_COUNT_INCREASE_MSG);
        /**
         * Move email to trash and check it
         */
        inboxPage = mailBoxPage.openInbox();
        inboxPage.selectRecievedEmail();
        inboxPage.clickMoveToTrash();
        //TODO check that confirm message appears:how to catch js elements
        Assert.assertTrue(inboxPage.isEmailDeletedMoved());
        /**
         * Refresh the page and check that email count hasn't been changed
         */
        int emailCount = inboxPage.setEmailCount();
        driver.navigate().refresh();
        newEmailCount = inboxPage.setEmailCount();
        Assert.assertEquals(newEmailCount, emailCount, EMAIL_COUNT_AFTER_REFRESH_MSG);
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
        driver.quit();
        /**
         * Kill all geckodriver.exe processes
         */
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        try {
            if (isDebug)
                Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
