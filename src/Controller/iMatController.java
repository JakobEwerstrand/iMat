package Controller;

import Model.*;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.Product;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The main controller for the application window.
 */
public class iMatController implements Initializable, WindowResizeObserver, Observer, AddProductObserver {
    private static BackendController backendController;

    private ProductBrowserController productBrowserController;
    private MyAccountController myAccountController;
    private CheckoutController checkoutController;
    private ShoppingCartController shoppingCartController;
    private Help help;


    private int cardSize = 300;
    private boolean isInfront = false;

    @FXML
    public AnchorPane contentPane;
    @FXML
    public TextField searchBar;

    @FXML
    public AnchorPane storeButton;
    @FXML
    public AnchorPane checkoutButton;
    @FXML
    public AnchorPane myAccountButton;
    @FXML
    public AnchorPane cartButton;

    private String activeColor = "#be5250";
    private String inActiveColor = "#ffffff";


    /**
     * Initializes iMatController
     * @param location location.
     * @param resources a resources file.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backendController = BackendController.getInstance();
        backendController.initCategories();

        productBrowserController = new ProductBrowserController();
        myAccountController = new MyAccountController();
        checkoutController = new CheckoutController();

        shoppingCartController = new ShoppingCartController();
        shoppingCartController.getShoppingCart1().register(this);
        observeAllProductCards();



        spawnProductBrowser();
        spawnMyAccount();
        spawnCheckout();
        spawnShoppingCart();
        spawnHelp();

        storeToFront();
    }

    /**
     * Shows the product browser
     */
    public void spawnProductBrowser() {
        ProductBrowser browser = productBrowserController.getProductBrowser();
        contentPane.getChildren().add(browser);
    }

    /**
     * Shows the my account view
     */
    public void spawnMyAccount() {
        MyAccount myAccount = myAccountController.getMyAccount();
        contentPane.getChildren().add(myAccount);
    }

    /**
     * Shows the my checkout view
     */
    private void spawnCheckout() {
        Checkout checkout = checkoutController.getCheckout();
        contentPane.getChildren().add(checkout);
    }

    private void spawnHelp() {
        help = new Help();
        contentPane.getChildren().add(help);
    }
    /**
     * Shows the shopping cart view
     */
    private void spawnShoppingCart() {
        ShoppingCart1 shoppingCart1 = shoppingCartController.getShoppingCart1();
        contentPane.getChildren().add(shoppingCart1);
    }

    /**
     * Brings the store view to the front.
     */
    public void storeToFront() {
        System.out.println("Bringing store view to front...");
        productBrowserController.getProductBrowser().toFront();
        storeActive(true);
        checkoutActive(false);
        myAccountActive(false);
    }

    /**
     * Brings the account view to the front.
     */
    public void myAccountToFront() {
        System.out.println("Bringing my account view to front...");
        myAccountController.getMyAccount().toFront();
        storeActive(false);
        checkoutActive(false);
        myAccountActive(true);
    }

    /**
     * Bring the checkout view to the front.
     */
    public void checkoutToFront() {
        System.out.println("Bringing checkout to front...");
        checkoutController.getCheckout().toFront();
        storeActive(false);
        checkoutActive(true);
        myAccountActive(false);
    }

    /**
     * Bring the help view to front.
     */
    public void helpToFront() {
        System.out.println("Bringing help to front...");
        help.toFront();
        help.populateFields();
    }

    /**
     * Bring the shopping cart view to the front.
     */
    public void shoppingCartToFront() {
        if (!isInfront) {
            System.out.println("Bringing my shopping cart view to front...");
            shoppingCartController.getShoppingCart1().toFront();
            isInfront = true;
        } else {
            shoppingCartController.getShoppingCart1().toBack();
            isInfront = false;
        }
    }


    /**
     * Calls all observers that the window has been resized.
     */
    @Override
    public void windowIsResized() {
        productBrowserController.updatePrefColumns(cardSize);
    }

    /**
     * Search
     */
    @FXML
    public void search() {
        String query = searchBar.getText();
        List<Product> products = backendController.search(query);
        productBrowserController.clearCardVBox();
        productBrowserController.spawnTitledSection("Sökresultat för: " + query);
        productBrowserController.spawnProductCardGrid(products);
    }

    private void storeActive(boolean state) {
        String color = (state) ? activeColor : inActiveColor;
        storeButton.setStyle("-fx-background-color: " + color);
        productBrowserController.showAllProducts();
//        storeButton.setStyle("-fx-text-fill: white");
    }

    private void myAccountActive(boolean state) {
        String color = (state) ? activeColor : inActiveColor;
        myAccountButton.setStyle("-fx-background-color: " + color);
    }

    private void checkoutActive(boolean state) {
        String color = (state) ? activeColor : inActiveColor;
        checkoutButton.setStyle("-fx-background-color: " + color);
    }



    @Override
    public void FromShoppingCartToCheckout() {
        checkoutToFront();
    }

    @Override
    public void productAdded(Product product) {

        final Animation animation = new Transition() {

            {
                setCycleDuration(Duration.millis(3000));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(0, 1, 0, 1 - frac);
                cartButton.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        animation.play();
    }

    @Override
    public void productRemoved(Product product) {

        final Animation animation = new Transition() {

            {
                setCycleDuration(Duration.millis(3000));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(1, 0, 0, 1 - frac);
                cartButton.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        animation.play();

    }

    private void observeAllProductCards() {
        for (ProductCard productCard : backendController.getProductCardMap().values()) {
            productCard.addObserver(this);
        }
    }
}
