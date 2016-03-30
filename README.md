#Cloud Vision Lib

This is a simple wrapper for Google's new Cloud Vision API. 

It uses the latest version of retrofit and the example app was built using the latest
preview of Android Studio (2.1 preview 4 at time of writing, with alpha gradle pluging) so please
be aware of that if you run into compiling issues.

##Usage 
First and foremost, to get access to the API, you need to follow the directions written here:

http://cloud.google.com/vision/docs/getting-started

<img src="https://raw.githubusercontent.com/trippedout/GoogleCloudVisionDemo/master/assets/setup.png" width="500"  />

Make sure you do everything in 'Setup up your project' in order to get an api key. You shouldn't need
to setup a Cloud Storage bucket for use with an Android app.

For using the library on its own, you can clone this repo and include the module, or, via `jcenter()`
you can add it to your `build.gradle dependencies {}`:

    compile 'net.trippedout:cloudvisionlib:0.1.0'
    
Once imported, you can start sending ImageRequests to the service.

Setup your `CloudVisionService`:

    mCloudVisionService = CloudVisionApi.getCloudVisionService();
    
Then, since there's only one API call (for now), get an image and determine what features you want
in your response, and call the API:

    String encodedData = ImageUtil.getEncodedImageData(path/to/file or Bitmap you want to use);
    
    Call<VisionResponse> call = mCloudVisionService.getAnnotations(
        "yourApiKey", // you get this key from the getting-started steps above        
        CloudVisionApi.getTestRequestAllFeatures(encodedData) // uses all possible features with default result number
    )

If you are familiar with `Retrofit` this should look familiar: `.enqueue()` the response, and use the built in
helper class or handle it like any other Retrofit call.
  
Check out [`ImageActivity#getFaces()`](https://github.com/trippedout/GoogleCloudVisionDemo/blob/master/app/src/main/java/net/trippedout/cloudvisiondemo/ImageActivity.java#L52)
for implementation specifics.

The `VisionResponse` object will contain a map of all the features you requested. In our example, we pass
all the face detect annotations to our custom `FaceFeaturesView`, an extension of `ImageView` that 
draws the annotations on top of its image, in the correct scale.

    CloudVisionApi.FaceDetectResponse faceDetectResponse 
        = (CloudVisionApi.FaceDetectResponse) response.getResponseByType(CloudVisionApi.FEATURE_TYPE_FACE_DETECTION);
    mFaceFeaturesView.setFaceAnnotations(faceDetectResponse.faceAnnotations);

<img src="https://raw.githubusercontent.com/trippedout/GoogleCloudVisionDemo/master/assets/demo.jpg" width="500"  />
