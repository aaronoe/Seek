# Seek

Seek is an Android App which is mostly written in Kotlin. It serves as an [Unsplash](https://unsplash.com) Client and can be used to browse through hundreds of thousands of photos.
The app has been published on the Play Store and can be [downloaded here](https://play.google.com/store/apps/details?id=de.aaronoe.seek&hl=en)

## Architecture

This project was my first attempt at trying out some of the popular architecture patterns. I decided to go with MVP because it felt like it suits my use case quite well. 
Every view has atleast one presenter associated with it, more presenters if that presenter's functionality is shared across mutltiple components of the app.

## Possible Improvements

For the future I hope to improve the app by injecting the Presenters and creating a real repository instead of the Retrofit client to make the model more abstract and seperate model logic from the presenter better. 

## External Libraries

- Dagger2 for dependency injection
- Retrofit2 as a REST-Client
- GSON for JSON serialization
- Picasso for image loading
- Butterknife for binding views

## License
This project utilizes the [MIT License](https://github.com/aaronoe/Seek/blob/master/LICENSE.md "Project License")
