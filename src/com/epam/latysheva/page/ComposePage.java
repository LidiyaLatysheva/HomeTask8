package com.epam.latysheva.page;

import org.openqa.selenium.WebDriver;


public class ComposePage extends MailBoxPage {

    public ComposePage(WebDriver driver) {
        super(driver);
    }

    public ComposePage fillToField(String toEmail) {
        waitForElementEnabled(TO_FIELD);
        driver.findElement(TO_FIELD).sendKeys(toEmail);
        return this;
    }

    public ComposePage fillSubjectField(String subject) {
        waitForElementEnabled(SUBJECT_FIELD);
        driver.findElement(SUBJECT_FIELD).sendKeys(subject);
        return this;
    }

    public ComposePage fillBodyField(String body) {
        driver.switchTo().frame(driver.findElement(BODY_FIELD_IFRAME));
        driver.findElement(BODY_FIELD).sendKeys(body);
        driver.switchTo().defaultContent();
        return this;
    }

    /**
     * Save email as Draft.
     */
    public ComposePage saveAsDraft() {
        driver.findElement(SAVE_MORE_DROPDOWN).click();
        driver.findElement(SAVE_DRAFT_MENU_ITEM).click();
        return this;
    }

    /**
     * Check that Email saved message appears.
     */
    public boolean isSavedToDrafts() {
        waitForElementVisible(SAVE_STATUS);
        String str = driver.findElement(SAVE_STATUS).getText();
        boolean isContained = str.contains(SAVED_TO_DRAFT_MSG);
        /**
         * Parsing saved message to get saving time for further verification.
         */
        String[] tmpWords = str.split("\\s");
        int size = tmpWords.length;
        saveTime = tmpWords[size - 1].toString();
        return isContained;
    }

    /**
     * Check that email body is the same as entered.
     */
    public boolean isBodyTheSame() {
        driver.switchTo().frame(driver.findElement(COMPOSE_EDITOR_IFRAME));
        waitForElementVisible(COMPOSE_EDITOR);
        String bodyField = driver.findElement(COMPOSE_EDITOR).getText();
        driver.switchTo().defaultContent();
        String str = EMAIL_DETAILS_BODY + EMAIL_SIGNATURE;
        return bodyField.equals(str);
    }

    public boolean isComposePage(){
        if (driver.findElement(SEND_BUTTON).isEnabled()){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Click Send button
     */
    public MailBoxPage clickSend(){
        waitForElementEnabled(SEND_BUTTON);
        driver.findElement(SEND_BUTTON).click();
        return new MailBoxPage(driver);
    }

    public void waitEmailSent(){
        waitForElementPresents(SENT_EMAIL_CONFIRMATION);
    }
}
