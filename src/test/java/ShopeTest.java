import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShopeTest {

    private static WebDriver driver;
    private static HashMap<String, String> variables = new HashMap<>();

    public static void main(String[] args) {
        switch ("chrome"){
            case "firefox":
                System.setProperty("webdriver.gecko.driver", "./drv/geckodriver.exe");
                driver = new FirefoxDriver();
                break;
            default:
                System.setProperty("webdriver.chrome.driver", "./drv/chromedriver.exe");
                driver = new ChromeDriver();
        }
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("https://chaihona.ru/");

//      Первый этап
        List<WebElement> mainMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']/li/a[text()]"));
        for (WebElement item : mainMenuItem) {
            if(item.getText().equalsIgnoreCase("Меню доставки")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        List<WebElement> subMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']//li[contains(@class,'level-2__item')]/a[text()]"));
        for (WebElement item : subMenuItem) {
            if(item.getText().equalsIgnoreCase("Бургеры и Шаурма")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        WebElement itemProduct = getProduct("Шаурма с курицей");
        String price = itemProduct.findElement(By.xpath(".//span[@class='price']")).getText().replaceAll("\\D","");
        setVariables("Шаурма с курицей", price);
        itemProduct.findElement(By.xpath(".//button[@title='Заказать']")).click();


//      Второй этап
        mainMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']/li/a[text()]"));
        for (WebElement item : mainMenuItem) {
            if(item.getText().equalsIgnoreCase("Меню доставки")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        subMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']//li[contains(@class,'level-2__item')]/a[text()]"));
        for (WebElement item : subMenuItem) {
            if(item.getText().equalsIgnoreCase("Выпечка")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        itemProduct = getProduct("Пичча нон с сыром");
        price = itemProduct.findElement(By.xpath(".//span[@class='price']")).getText().replaceAll("\\D","");
        setVariables("Пичча нон с сыром", price);
        itemProduct.findElement(By.xpath(".//button[@title='Заказать']")).click();


//      Третий этап
        mainMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']/li/a[text()]"));
        for (WebElement item : mainMenuItem) {
            if(item.getText().equalsIgnoreCase("Меню доставки")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        subMenuItem = driver.findElements(By.xpath("//ul[@id='desktopMenuMain']//li[contains(@class,'level-2__item')]/a[text()]"));
        for (WebElement item : subMenuItem) {
            if(item.getText().equalsIgnoreCase("Плов")){
                new WebDriverWait(driver, 10)
                        .until(ExpectedConditions.elementToBeClickable(item)).click();
                break;
            }
        }
        itemProduct = getProduct("Плов чайханский");
        assert itemProduct != null;
        price = itemProduct.findElement(By.xpath(".//span[@class='price']")).getText().replaceAll("\\D","");
        setVariables("Плов чайханский", price);
        itemProduct.findElement(By.xpath(".//button[@title='Заказать']")).click();

//      Четыертый этап
        WebElement goToCart = driver.findElement(By.xpath("//a[@title='Перейти в корзину']"));
        goToCart.click();

        Assert.assertTrue(String.format("товар %s не найден в корзине", "Пичча нон с сыром"),
                productExist("Пичча нон с сыром"));
        Assert.assertTrue(String.format("товар %s не найден в корзине", "Шаурма с курицей"),
                productExist("Шаурма с курицей"));
        Assert.assertTrue(String.format("товар %s не найден в корзине", "Плов Чайханский"),
                productExist("Плов Чайханский"));


        Integer expectedAmount = Integer.parseInt(getVariable("Пичча нон с сыром")) + Integer.parseInt(getVariable("Шаурма с курицей")) +
                Integer.parseInt(getVariable("Плов чайханский"));

        WebElement totalAmount = driver.findElement(By.xpath("//*[contains(text(),'Итого к оплате:')]/parent::div//span[@class='price']"));
        Assert.assertEquals(String.format("Итоговая сумма не равна значению %s. Получено значение %s", expectedAmount.toString(), totalAmount.getText().replaceAll("\u20BD", "")),
                expectedAmount.toString(), totalAmount.getText().replaceAll(" \u20BD", ""));

        driver.quit();



    }


    public static WebElement getProduct(String productName){
        List<WebElement> productCollection = driver.findElements(By.xpath("//ul[@class='products-grid']//*[@title]/ancestor::li[@class='product-item']"));
        for (WebElement item : productCollection){
            if (item.findElement(By.xpath(".//a[@title]")).getAttribute("title").equalsIgnoreCase(productName)){
                return item;
            }
        }
        Assert.fail("Не найден продукт - " + productName);
        return null;
    }

    public static boolean productExist(String productName){
        List<WebElement>  shoppingCartTable = driver.findElements(By.xpath("//table[@id = 'shopping-cart-table']//tr[@class='cart-item']"));
        for (WebElement item : shoppingCartTable){
            item = item.findElement(By.xpath(".//td[@class='product-name']/a"));
            if (item.isDisplayed() && item.getText().equalsIgnoreCase(productName)){
                return true;
            }
        }
        return false;
    }


    public static String getVariable(String key){
        return variables.get(key);
    }

    public static void setVariables(String key, String value){
        variables.put(key, value);
    }

}
