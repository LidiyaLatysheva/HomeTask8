package com.epam.latysheva.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class HomePage extends Page {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(START_URL);
        return this;
    }

    public InboxPage login(String login, String password) {
        driver.findElement(LOGIN_FIELD).sendKeys(login);
        driver.findElement(PASSWORD_FIELD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();
        return new InboxPage(driver);
    }

    public boolean isHomePage(){
        if (driver.findElement(LOGIN_BUTTON).isEnabled() &
                driver.findElement(LOGIN_FIELD).isEnabled() &
                driver.findElement(PASSWORD_FIELD).isEnabled()){
            return true;
        } else{
            return false;
        }
    }
}
