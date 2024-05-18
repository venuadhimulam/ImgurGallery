# ImgurGallery Application

## Overview

This application allows users to create accounts, log in, and manage their image galleries using Imgur's API.

## Getting Started

### Prerequisites

- Java 17
- Spring Boot
- H2 Database
- Imgur API credentials

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/imgurGallery.git
    cd imgurGallery
    ```

2. Update the `application.properties` file with your Imgur API credentials and other necessary configurations.

3. Build and run the application:
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```

### Usage

#### Create a User

1. **Endpoint:** `POST /user/v1/createUser`
2. **Request Body:**
    ```json
    {
        "username": "GT",
        "password": "test@mail.com",
        "gender": "Male",
        "email": "test@mail.com"
    }
    ```
3. **Description:** Creates a new user with the provided details.

#### Log in

1. **Endpoint:** `POST /user/v1/login`
2. **Request Body:**
    ```json
    {
        "username": "GT",
        "password": "test@mail.com"
    }
    ```
3. **Description:** Logs in with the provided credentials and returns an access token.

#### Authorization

Include the following header in requests for saving, getting, or deleting images:

- **Key:** `Authorization`
- **Value:** `Bearer {{accessToken}}`

Replace `{{accessToken}}` with the token received from the login response.

#### Save an Image

1. **Endpoint:** `POST /images/saveImage`
2. **Headers:**
    ```sh
    Authorization: Bearer {{accessToken}}
    ```
3. **Request Body:** (Example for uploading an image)

#### Get an Image

1. **Endpoint:** `GET /images/getImage/{imageId}`
2. **Headers:**
    ```sh
    Authorization: Bearer {{accessToken}}
    ```
3. **Path Parameter:** `imageId` - ID of the image to retrieve.

#### Delete an Image

1. **Endpoint:** `DELETE /images/deleteImage/{imageId}`
2. **Headers:**
    ```sh
    Authorization: Bearer {{accessToken}}
    ```
3. **Path Parameter:** `imageId` - ID of the image to delete.

**Note:** The `imageId` can be obtained from the `ImageEntity` table in the database.
