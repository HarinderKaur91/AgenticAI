package com.Harinder.Playwright.Pages;
import com.microsoft.playwright.Page;

public class ContactUsPage {

    private final Page page;

    public ContactUsPage(Page page) {
        this.page = page;
    }

    public boolean isContactUsPageVisible() {
        page.locator("text=Get In Touch").waitFor();
        return page.locator("text=Get In Touch").isVisible();
    }

    public void submitContactForm(String name, String email, String subject, String message, String filePath) {
        page.locator("input[data-qa='name']").fill(name);
        page.locator("input[data-qa='email']").fill(email);
        page.locator("input[data-qa='subject']").fill(subject);
        page.locator("textarea[data-qa='message']").fill(message);
        page.locator("input[name='upload_file']").setInputFiles(java.nio.file.Path.of(filePath));

        page.onceDialog(dialog -> dialog.accept());
        page.locator("input[data-qa='submit-button']").click();
    }

    public String getSuccessMessage() {
        page.locator(".status.alert.alert-success").waitFor();
        return page.locator(".status.alert.alert-success").textContent().trim();
    }
}
