# EVO Cybersecurity Module

## Deeptech GigaHack 2025  
**Electronic Governance Agency x eGA Challenge**

### Project: EVO Cybersecurity Module Prototype

---

## Overview

This project is a prototype cybersecurity module designed for integration with the EVO application, developed as part of the Deeptech GigaHack 2025 challenge. The goal is to empower users with tools to check suspicious addresses and files, receive notifications about the latest cyber threats, and report cyber incidents, all while contributing to national cybersecurity.

> **Mission:**  
> Develop a self-contained cybersecurity software module for EVO that:
> - Verifies suspicious URLs and files
> - Notifies users about the latest cyber threats
> - Enables simplified cyber incident reporting

---

## Functional Requirements

### Implemented Features

- **URL/File Verification**
  - Graphical interface for submitting suspicious URLs (and files)
  - Queries a public API (e.g., VirusTotal, Google Safe Browsing, URLScan.io)
  - Displays results: Safe, Suspicious, or Malicious

- **Cyber Alerts**
  - Section displaying the latest threats, risks, and vulnerabilities
  - Fetches alerts from official sources, such as:
    - [cyberevent.gov.md](https://cyberevent.gov.md)
    - [stisc.gov.md/ro/alerte](https://stisc.gov.md/ro/alerte)
  - Shows titles and content/links

- **Simplified Incident Reporting**
  - Form for reporting security incidents
  - Collects: Name, Email, Incident Description, Attachments (images/screenshots)
  - Structures report data in a predefined (e.g., JSON) format, ready for transmission

### Not Implemented (Bonus Features)

- **Micro-learning Module (Not implemented)**
  - Interactive security learning/testing module with scoring and feedback

- **QR Code Scanner & Text Extraction (Not implemented)**
  - Functionality to scan QR codes and extract/verify links from scanned text

---

## Design & UX/UI Guidelines

- **Consistency:**  
  Interface aligns with EVO's design language (colors, icons, typography).

- **Clarity & Simplicity:**  
  Extremely intuitive UI, clear notifications (using red/yellow/green), and recognizable icons.

- **Accessibility:**  
  Sufficient color contrast, legible fonts, and responsive layouts for various platforms and screen sizes.

---

## Technology Stack & Integration

- **Platforms:**  
  - Android: Kotlin
  - Modular architecture, intended for integration (not standalone)

- **APIs:**  
  - Use public APIs (e.g., VirusTotal, Google Safe Browsing, URLScan.io)
  - Proprietary/paid APIs are permitted but not advantageous for scoring

- **Data Management:**  
  - No storage/logging of personally identifiable information
  - All API data handling must comply with privacy terms and data anonymization principles

---



## Contributing

Contributions are welcome!
---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Acknowledgments

- Deeptech GigaHack 2025
- Electronic Governance Agency (AGE)
- eGA
-ETEAM
