package client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

public class BasicProject implements EntryPoint, ValueChangeHandler<String> {

    private static final String LOGO_IMAGE_NAME = "gwtia.png";

    /**
     * This TabLayoutPanel will hold the application's 3 "pages" of content.
     */
    TabLayoutPanel content;

    /**
     * The HTMLPanels that will hold the content we want to display in the TabPanel.
     */
    HTMLPanel homePanel;
    HTMLPanel productsPanel;
    HTMLPanel contactPanel;

    /**
     * This button  will wrap the existing HTML button defined in the HTML page and
     * is used for the dummy search capability.
     */
    Button search;

    /**
     * This panel  sits on the right hand side of the page to allow user feedback.
     * It will slide in when the mouse is over it and slides back out again if the
     * mouse moves off it.
     */
    FocusPanel feedback;

    /**
     * The image logo.
     */
    Image logo;

    /**
     * A popup panel that will be displayed if the search button is selected.
     */
    PopupPanel searchRequest;

    public void onModuleLoad() {
        // Create the user interface
        setUpGui();
        // Set up history management
        setUpHistoryManagement();
        // Set up all the event handling required for the application.
        setUpEventHandling();
    }

    private void setUpEventHandling() {
        /**
         *  If a tab is selected then we want to add a new history item to the History object.
         *  (this effectively changes the token in the URL, which is detected and handled by
         *  GWT's History sub-system.
         */
        content.addSelectionHandler(new SelectionHandler<Integer>(){
            public void onSelection(SelectionEvent<Integer> event) {
                // Determine the tab that has been selected by interrogating the event object.
                Integer tabSelected = event.getSelectedItem();

                // Create a new history item for this tab (using data retrieved from Pages enumeration)
                History.newItem(Pages.values()[tabSelected].getText());
            }
        });


        /**
         *  If the search button is clicked, we want to display a little pop-up panel which allows
         *  the user to type in a search term.  The TextBox where the user types search terms should
         *  automatically gain focus to make it more user friendly.
         */
        search.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
                FlowPanel qAnswer;
                final TextBox searchTerm = new TextBox();

                // If search button is clicked for the first time then the searchRequest Pop-up panel does not yet exist
                // so we'll build it first as follows:
                if (searchRequest==null){
                    // Create the PopupPanel widget
                    searchRequest = new PopupPanel();

                    // Create a FlowPanel to hold the question and answer for the search term
                    qAnswer = new FlowPanel();
                    // Add a Label to the Flow Panel that represents the "Search For" text
                    qAnswer.add(new Label("Search For:"));
                    // Add the answer TextBox (which we declared above) to the FlowPanel
                    qAnswer.add(searchTerm);

                    // Add a change handler to the TextBox so that when there is a change to search term
                    // we would "start" the search (we don't implement the search capability in this simple example)
                    searchTerm.addChangeHandler(new ChangeHandler(){
                        public void onChange(ChangeEvent event) {
                            // Hide the popup panel from the screen
                            searchRequest.hide();
                            // "start" the search
                            Window.alert("If implemented, now we would search for: "+searchTerm.getText());
                        }
                    });

                    // Add the question/answer to the search pop-up.
                    searchRequest.add(qAnswer);

                    // Now we'll set some properties on the pop up panel, we'll:
                    // * indicate that the popup should be animated
                    // * show it relative to the search button widget
                    // * close it if the user clicks outside of it popup panel, or if the history token is changed
                    searchRequest.setAnimationEnabled(true);
                    searchRequest.showRelativeTo(search);
                    searchRequest.setAutoHideEnabled(true);
                    searchRequest.setAutoHideOnHistoryEventsEnabled(true);
                } else {
                    // search popup already exists, so clear the TextBox contents...
                    searchTerm.setText("");
                    // ... and simply show it.
                    searchRequest.show();
                }

                // Set the TextBox of the popup Panel to have focus - this means that once the pop up is displayed
                // then any keypresses the user makes will appear directly inthe TextBox.  If we didn't do this, then
                // who knows where the text would appear.
                searchTerm.setFocus(true);
            }
        });

        /**
         * If the user moves mouse over feedback tab, change its style
         * (increases its size and changes colour - styles are in BasicProject.css)
         */
        feedback.addMouseOverHandler(new MouseOverHandler(){
            public void onMouseOver(MouseOverEvent event) {
                // Remove existing normal style
                feedback.removeStyleName("normal");
                // Add the active style
                feedback.addStyleName("active");
                // Set overflow of whole HTML page to hidden  to minimise display of scroll bars.
                RootPanel.getBodyElement().getStyle().setProperty("overflow", "hidden");
            }
        });

        /**
         * If use moves mouse out of the feedback panel, return its style to normal
         * (decreases its size and changes colour - styles are in BasicProject.css)
         */
        feedback.addMouseOutHandler(new MouseOutHandler(){
            public void onMouseOut(MouseOutEvent event) {
                feedback.removeStyleName("active");
                feedback.addStyleName("normal");
                RootPanel.getBodyElement().getStyle().setProperty("overflow", "auto");
            }
        });

        /**
         * If user clicks on the feedback tab we should start some feedback functionality.
         * In this example, it simply displays an alert to the user.
         */
        feedback.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
                Window.alert("You could provide feedback if this was implemented");
            }
        });
    }

    private void setUpHistoryManagement() {
        History.addValueChangeHandler(this);
        History.fireCurrentHistoryState();
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(Window.ClosingEvent event) {
                event.setMessage("Ran out of history.  Now leaving application, is that OK?");
            }
        });
    }

    private void setUpGui() {
        // Build the TabPanel content from existing HTML page text
        buildTabContent();
        // Wrap the existing search button
        wrapExisitngSearchButton();
        // Insert a logo into a defined slot in the HTML page
        insertLogo();
        //Create the Feedback tab on the right of the page
        createFeedbackTab();

        // Style the TabPanel using methods from the UIObject it inherits
        styleTabPanelUsingUIObject();
        // Style the Button using low level DOM access
        styleButtonUsingDOM();

        // Add the feedback panel directly to the page
        RootPanel.get().add(feedback);
        // Add the logo to the DOM element with id of "logo"
        RootPanel logoSlot = RootPanel.get("logo");
        if (logoSlot!=null)logoSlot.add(logo);
        // Add the TabPanel to the DOM element with the id of "content"
        RootPanel contentSlot = RootPanel.get("content");
        if (contentSlot!=null) contentSlot.add(content);
        // There's no need to add the button, as it is already in the original HTML page.
    }

    private void styleButtonUsingDOM() {
        search.getElement().getStyle().setProperty("backgroundColor", "#ff0000");
        search.getElement().getStyle().setProperty("border", "2px solid");
        search.getElement().getStyle().setOpacity(0.7);
    }

    private void styleTabPanelUsingUIObject() {
        homePanel.setHeight("400px");
        productsPanel.setHeight("400px");
        contactPanel.setHeight("400px");
        content.setHeight("420px");
    }

    private void createFeedbackTab() {
        feedback = new FocusPanel();
        feedback.setStyleName("feedback");
        feedback.addStyleName("normal");
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(new Label("Feed"));
        verticalPanel.add(new Label("Back"));
        feedback.add(verticalPanel);
    }


    private void buildTabContent() {
        homePanel = new HTMLPanel(getContent(Pages.HOME.getText()));
        productsPanel = new HTMLPanel(getContent(Pages.PRODUCTS.getText()));
        contactPanel = new HTMLPanel(getContent(Pages.CONTACT.getText()));
        homePanel.addStyleName("htmlPanel");
        productsPanel.addStyleName("htmlPanel");
        contactPanel.addStyleName("htmlPanel");
        content = new TabLayoutPanel(20, Style.Unit.PX);
        content.add(homePanel, Pages.HOME.getText());
        content.add(productsPanel, Pages.PRODUCTS.getText());
        content.add(contactPanel, Pages.CONTACT.getText());
        content.selectTab(Pages.HOME.getVal());
    }

    private String getContent(String id) {
        String toReturn = "";
        // Find the DOM element by the id passed in.
        Element element = DOM.getElementById(id);
        if (element != null) {
            toReturn = DOM.getInnerHTML(element);
            DOM.setInnerText(element, "");
            SafeHtml safeHtml = SimpleHtmlSanitizer.sanitizeHtml(toReturn);
            toReturn = safeHtml.asString();
        } else {
            toReturn = "Unable to find "+id+" content in HTML page";
        }
        return toReturn;
    }

    private void wrapExisitngSearchButton() {
        // Try and find the DOM element
        Element el = DOM.getElementById("search");

        // If the element is not null, then we've found it, so let's wrap it
        if(el!=null){
            search = Button.wrap(el);
        } else {
            // The search button is missing in the underlying HTML page, so we can't wrap it...
            // Let's log the fact it is missing - in development mode this will appear
            // in the console, in web mode the code will be compiled out
            GWT.log("The search button is missing in the underlying HTML page, so we can't wrap it...trying to create it instead");
            // We should play safe and create it manually and throw it on the page somewhere - otherwise we risk having
            // null pointer exceptions elsewhere in our application as the button doesn't exist yet.
            search = new Button("search");
            RootPanel.get().add(search);
        }

    }

    private void insertLogo() {
        logo = new Image(GWT.getModuleBaseURL() + "../" + LOGO_IMAGE_NAME){
            @Override
            public void onBrowserEvent(Event event) {
                event.preventDefault();
                super.onBrowserEvent(event);
            }
        };
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        // Get the token from the event
        String page = event.getValue().trim();
        // Check if the token is null or empty
        if ((page == null) || (page.equals(""))) {
            showHomePage();
            // Else check what the token is and cal the appropriate method.
        } else if (page.equals(Pages.PRODUCTS.getText())) {
            showProducts();
        } else if (page.equals(Pages.CONTACT.getText())) {
            showContact();
        } else {
            showHomePage();
        }
    }

    /**
     * Show the contact page - i.e. place a new label on the current screen
     */
    private void showContact() {
        content.selectTab(Pages.CONTACT.getVal());
    }

    /**
     * Show the home page - i.e. place a new label on the current screen
     */
    private void showHomePage() {
        content.selectTab(Pages.HOME.getVal());
    }

    /**
     * Show the products page - i.e. place a new label on the current screen
     */
    private void showProducts() {
        content.selectTab(Pages.PRODUCTS.getVal());
    }
}
