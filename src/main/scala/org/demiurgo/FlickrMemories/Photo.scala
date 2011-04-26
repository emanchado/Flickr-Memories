import scalaj.http.Http
import scala.xml._

package org.demiurgo.FlickrMemories {
  class Photo(var id: String, var secret: String, var userId: String,
              var farmId: String, var serverId: String, var title: String) {
    var _description : String = null

    def imageUrl : String = {
      return "http://farm" + farmId + ".static.flickr.com/" +
                serverId + "/" + id + "_" + secret + ".jpg"
    }

    def pageUrl : String = {
      return "http://www.flickr.com/photos/" + userId + "/" + id
    }

    def extractDescriptionFromFlickResponse(xml: String) : String = {
      val dom = XML.loadString(xml)
      return ((dom \\ "photo")(0) \ "description").text
    }

    def fetchPhotoDescription : String = {
      val xml = Http("http://api.flickr.com/services/rest").
                  param("method",         "flickr.photos.getinfo").
                  param("api_key",        App.API_KEY).
                  param("photo_id",       id).asString
      return extractDescriptionFromFlickResponse(xml)
    }

    def description : String = {
      if (_description == null) {
        _description = fetchPhotoDescription
      }

      return _description
    }
  }
}
