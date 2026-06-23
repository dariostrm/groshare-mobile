# GroShare

GroShare is a collaborative grocery list application for Android, iOS, and Desktop. It allows users to manage a shared shopping list and track expenses among a group.

## Usage

- **Login**: You can use the credentials `testuser` and `testpassword`, or register through the website.
- Click the (+) button, enter the item name in the dialog, and click OK.
- While shopping, click on items to select them.
- Once items are selected, click the **cart icon** (which replaces the plus icon) to open the checkout dialog.
- Enter the total price to split it equally among selected items. Switch to **itemized** mode to edit individual prices.
- View your net balance and individual debts on the **Debts** page.
- Click **Settle** on the debts page when you gave money back to the person you owed it.
- **Profile**: Change the application theme (light, dark, or system) or log out

## Build and Deploy

You can also download a release on the GitHub page
[Here](https://github.com/dariostrm/groshare-mobile/)

### Prerequisites

- **JDK 21**
- **Android SDK** (API 36)
- **Android Studio** or **IntelliJ IDEA**
- **Xcode** (Required for iOS builds on macOS)

### Setup

1. Clone the repository to your local machine
```bash
git clone https://github.com/dariostrm/groshare-mobile.git
```
2. Open the project in your IDE.
3. Allow Gradle to sync and download dependencies.

### Running the Application

#### Android
Use the `androidApp` run configuration in your IDE, or run:
```bash
./gradlew :androidApp:installDebug
```

#### Desktop
Use the `desktopApp` run configuration in your IDE, or run:
```bash
./gradlew :desktopApp:run
```

#### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and click the **Run** button.
