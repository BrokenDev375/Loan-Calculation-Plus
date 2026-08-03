# Implementation Plan: Loan Calculation Plus

## Summary

Convert the current Android app from an installed-app manager into Loan Calculation Plus based on `loan-calculation-plus-mockup.html`.

Keep the AAR startup and monetization flow:

`Splash -> Consent -> Interstitial -> Language -> IAP -> MainActivity -> Intro/Home`

Use Kotlin, Jetpack Compose, Hilt, Room, and a default VND currency. Exchange rates are offline in v1.

## Product And Navigation

- Replace the current app-manager navigation with four tabs: Home, Tools, Compare, Settings.
- Home includes Personal Loan, Business Loan, Mortgage, Auto Loan, Fixed Deposit, Recurring Deposit, and recent History.
- Add calculator form, result, history list/detail, compare detail, converter, world clock, add-clock, and settings screens.
- Adapt Intro content to Loan Calculation Plus.
- Remove installed-app, scan/update, device-info, uninstall, usage-access, and other legacy routes from the active navigation.
- Keep `applicationId` and Firebase identity unchanged; change the display name to `Loan Calculation Plus`.

## Domain And Calculators

Create a `domain/calculator` layer with independent models and use cases:

- Loan calculator for Personal, Business, and Auto Loan.
  - Inputs: principal, annual interest rate, term in months/years.
  - Outputs: monthly payment, total payment, total interest, payoff date.
  - Support zero-interest loans.
- Mortgage calculator.
  - Inputs: home price, down-payment amount/percentage, term, rate, property tax, PMI, HOA, insurance.
  - Treat property tax, PMI, and insurance as annual values; treat HOA as monthly. Label units clearly in the UI.
  - Output total monthly payment and payment breakdown.
- Deposit calculator for FD and RD.
  - FD: one-time principal, annual rate, tenure, compounding frequency.
  - RD: monthly deposit, annual rate, tenure.
  - Output maturity value, invested/deposited amount, interest earned, maturity date.
- Offline converters for exchange rate, temperature, mass, speed, and length.
- World Clock using `java.time.ZoneId` and actual timezone data.
- Use `BigDecimal` in financial calculations and round only for display.

## Persistence And Data

Extend Room with:

- `CalculationHistory`: id, calculator type, created date, serialized input, serialized result.
- `CompareItem`: id, calculator type, history id, display order.
- `WorldClockEntry`: id, city, zone id, display order.

Repositories must support saving, listing, filtering by Calculator/Investment, detail lookup, deleting individual records, clearing all history, managing compare items, and managing world-clock entries.

History, Compare, and World Clock data must survive app restarts.

## Results, Compare, And PDF

- Use one shared Result screen for all calculators.
- Show input summary, payment/maturity result, total interest, mortgage breakdown, and loan amortization table where applicable.
- `Add to Compare` saves the result to Room and opens Compare detail.
- Compare supports multiple results of the same calculator type, horizontal paging, and deletion.
- `Share PDF` creates a real PDF with Android `PdfDocument` and shares it through the existing `FileProvider`.
- Do not keep the mockup's Toast-only implementations.

## Ads, IAP, And AAR

- Keep AAR-managed Splash, consent, startup interstitial, Language, IAP, notification, and resume-ad behavior.
- Use `Admob.getInstance()` for app ads only.
- Add native placements for Home, Tools, Compare, Settings, and Result.
- Use interstitials before major calculator navigation where enabled by Remote Config.
- Premium uses `IAPUtils.isPremium()` and hides all ads.
- Ensure every ad callback continues navigation exactly once on success, no-fill, or error.
- Verify all IAP product IDs against Play Console before release; do not ship mismatched IDs.

## Cleanup

- Remove legacy app-manager routes and screens from active navigation.
- Remove dead route declarations that are not connected to `NavHost`.
- Keep `MainActivity`, `MyApplication`, AAR integration, ads core, and Room infrastructure where reusable.
- Replace emoji-based UI icons with Android resources/material icons.

## Tests And Acceptance

### Unit tests

- Loan calculations with zero and nonzero interest, month/year terms, and invalid input.
- Mortgage with zero/percentage down payment and all additional costs.
- FD compounding frequencies and RD monthly deposits.
- Bidirectional converters and timezone handling.

### Room tests

- History CRUD, filtering, clear-all, Compare CRUD, and World Clock CRUD.

### Navigation tests

- AAR startup through Language, IAP, and Home.
- Intro to Home.
- Home to every calculator, Result, History, Compare, Tools, and Settings.
- Back navigation from History detail, Compare detail, Converter, and World Clock.
- Premium disables ads; ad failure still continues navigation.

### Build/device acceptance

- `assembleDebug` succeeds.
- Release build succeeds with R8 and resource shrinking after the real keystore is restored.
- Test fresh install, relaunch, language change, premium restore, no-fill ads, process death, rotation, PDF sharing, and all back-navigation paths on a physical device.

## Fixed Assumptions

- Display name: `Loan Calculation Plus`.
- Default currency: Vietnamese Dong (VND).
- Offline exchange rates for v1, designed for later API replacement.
- Room is the source of truth for History, Compare, and World Clock.
- AAR startup, Language, IAP, consent, and ads remain enabled.
- Release acceptance requires `app/keystore/update.jks`.
