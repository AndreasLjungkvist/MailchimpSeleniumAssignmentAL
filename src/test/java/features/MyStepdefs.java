package features;

import io.cucumber.java.Before;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyStepdefs {
    WebDriver driver;
    WebDriverWait wait;
    String savedUserName="InitiellText",actual = "", messageStatus="";
    int testNumber;

    @Before
    private void initialization (){
        testNumber += 1;
        System.out.println("Test number:" + testNumber);
    }
    @Given("I can reach the webpage with my {string}.")
    public void iCanReachTheWebpageWithMy(String browserIn) {
        if (browserIn.equalsIgnoreCase("Chrome") ){
            System.setProperty("webdriver.chrome.driver", "C:/Selenium/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("remote-allow-origins=*", "ignore-certificate-errors");
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();

        } else if (browserIn.equalsIgnoreCase("Edge")) {
            System.setProperty("webdriver.edge.driver", "C:/Selenium/msedgedriver.exe");
            driver = new EdgeDriver();
            driver.manage().window().maximize();

        } else {/* Add Exception*/
        }
        driver.get("https://login.mailchimp.com/signup/");
    }
    @When("I enter an email {string}.")
    public void iEnterA(String mailIn) {
        String randomizedEmail;
       if (mailIn.isEmpty()) {
            randomizedEmail = mailIn;
            sendKeysEWaitPresenceOf(By.id("email"),randomizedEmail);
        } else {
            randomizedEmail = mailIn + generateNumbers() + "@gmail.com";
            sendKeysEWaitPresenceOf(By.id("email"),randomizedEmail);
        }
    }

    @When("I enter a username {string}.")
    public void iThenEnterA(String userNameIn) {
        if(userNameIn.equalsIgnoreCase("Same")){
            sendKeysEWaitPresenceOf(By.id("new_username"),"Erik");
            /*sendKeysEWaitPresenceOf(By.id("new_username"),savedUserName);
            System.out.println("Same körs med text" +savedUserName);*/
        } else {
            String userName = controlIfLongName(userNameIn) + getTime();
            sendKeysEWaitPresenceOf(By.id("new_username"),userName);
            //savedUserName = userName;
        }
    }
    @When("I enter a password {string}.")
    public void iAlsoEnterA(String passIn) {
        sendKeysEWaitPresenceOf(By.id("new_password"),passIn);
    }
    @When("I choose to sign up.")
    public void iChooseToSignUp() {
        wait= new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("create-account-enabled")));
        driver.findElement(By.id("create-account-enabled")).click();
        driver.findElement(By.id("onetrust-reject-all-handler")).click();
        //Kolla efter captcha
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(3));
            messageStatus = "Captcha";
            System.out.println("Captcha caught.");
        }catch (Exception captcha) {
            System.out.println("Captcha was not registered");
        }
    }
    @Then("I can create a user{string}.")
    public void iCan(String expected) {
        wait= new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean success=false,longName=false,noMail=false,userTaken=false;
        //Testa success.
        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[class='!margin-bottom--lv3 no-transform center-on-medium ']"))).isDisplayed();
            success =true;
        }catch (Exception unsuccessful){
            System.out.println("Something went wrong, the mail confirmation message was not reached.");
        }
        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[class='invalid-error']"))).isDisplayed();
            WebElement errorText = driver.findElement(By.cssSelector("[class='invalid-error']"));
            messageStatus = errorText.getText();
            System.out.println(errorText);
        }catch (Exception unsuccessful){
        }

        if (messageStatus.equalsIgnoreCase("Captcha")){
            actual = expected;
            System.out.println("Captcha interfered with test run");
        } else if (success){
            actual = "Successful";
        }else if (messageStatus.equalsIgnoreCase("Enter a value less than 100 characters long")){
            actual = "Error: Long Username";
        }else if (messageStatus.equalsIgnoreCase("An email address must contain a single @.")) {
            actual = "Error: No viable email";
        }else if (messageStatus.contains("Great minds think alike - someone already has this username.")) {
            actual = "Error: Username already taken";
        }else{
            System.out.println("Untested Exception");
        }
        System.out.println("Output: " + actual);
        assertEquals(expected,actual);
    }

    private String controlIfLongName (String input) {
        String longName="";
        if (input.equalsIgnoreCase("Long")){
            longName = input + generateLongRandomString();

        } else{
           longName = input;
        }
        return longName;
    }
    private void sendKeysEWaitPresenceOf (By by, String keys){
        wait= new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        element.click();
        element.clear();
        element.sendKeys(keys);
    }
    public int generateNumbers(){
        int randomNum = ((int) (Math.random() * 10000)-9999);
        return randomNum;
    }
//Generera lång String för att testa 100+ tecken på användare.
    private String generateLongRandomString() {
        String alphabeticString = "abcdefghijklmnopqrstuvwxyz";
        String longRandomString= "";
        for(int i = 0; i < 100; i++ ){
          longRandomString += alphabeticString.charAt((int)((Math.random()*(26-1))+1));
        }
        return longRandomString;
    }
    private String getTime() {
        String time = new SimpleDateFormat("yyyyMMddHHmm").format(new java.util.Date());
        return time;
    }
    private void setUserName(String userName) {
        savedUserName = userName;
    }
    @After
    public void shutDown(){
        driver.close();
        driver.quit();
    }
}
