// JES. US. CHRIST.
import java.util.{Calendar,Date}
import java.text.SimpleDateFormat
// Template system
import java.io.StringWriter
import org.apache.velocity.app.Velocity
import org.apache.velocity.VelocityContext
// Mail
import javax.mail._
import javax.mail.internet.{InternetAddress,MimeMessage}

package org.demiurgo.FlickrMemories {
  object App {
    val API_KEY       = "30c195ccce757cd281132f0bef44de0d"
    val TEMPLATE_PATH = "template.html"

    def findThisWeekInPastYear(referenceDate: Date, numberOfYears: Int = 1) : (Date, Date) = {
      val c = Calendar.getInstance
      c.setTime(referenceDate)
      c.add(Calendar.YEAR, -1 * numberOfYears)
      while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        c.add(Calendar.DATE, -1)
      }
      val startDate = c.getTime
      c.add(Calendar.DATE, 7)
      val endDate = c.getTime
      return (startDate, endDate)
    }

    def htmlForPhotos(photos: Seq[Photo], dateSince: Date, dateUntil: Date) : String = {
      Velocity.init
      val context = new VelocityContext
      val niceDateFormatter = new SimpleDateFormat("MMM d")
      val yearDateFormatter = new SimpleDateFormat("yyyy")
      context.put("dateSince", niceDateFormatter.format(dateSince))
      context.put("yearSince", yearDateFormatter.format(dateSince))
      context.put("dateUntil", niceDateFormatter.format(dateUntil))
      context.put("photos",    photos.toArray)
      val w = new StringWriter
      Velocity.mergeTemplate("template.vm", context, w)
      return w.toString
    }

    def mail(to: String, subject: String, body: String) {
      val props = System.getProperties()
      val conf  = new Configuration("config.yml")
      props.put("mail.smtp.host", conf.get("Mail", "host"))
      props.put("mail.smtp.port", conf.get("Mail", "port"))

      val session = Session.getDefaultInstance(props, null)
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(conf.get("Mail", "from")))
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
      message.setSubject(subject)
      message.setContent(body, "text/html; charset=\"utf-8\"")
      Transport.send(message)
    }

    def main(args: Array[String]) {
      val userId           = args(0)         // 24881879@N00
      var referenceDate    = new Date        // today by default
      var mailRecipient    = ""
      val rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd")
      if (args.size > 1) {
        referenceDate = rfc3339Formatter.parse(args(1))
      }
      if (args.size > 2) {
        mailRecipient = args(2)
      }

      val engine = new SearchEngine()

      var years = 1
      var picsFound = false
      do {
        val (dateSince, dateUntil) = findThisWeekInPastYear(referenceDate,
                                                            years)
        val dateSinceString = rfc3339Formatter.format(dateSince)
        val dateUntilString = rfc3339Formatter.format(dateUntil)

        val photos = engine.searchByUserAndDateRange(userId, dateSinceString, dateUntilString)
        if (photos.length > 0) {
          picsFound = true
          val output = htmlForPhotos(photos, dateSince, dateUntil)
          if (mailRecipient != "") {
            mail(mailRecipient,
                 "FlickMemories " + dateSinceString + " - " + dateUntilString,
                 output)
          } else {
            println(output)
          }
        }
        years += 1
      } while (years < 6 && !picsFound)

      if (! picsFound && mailRecipient == "") {
        println("Didn't find any photos for " + args(1))
      }
    }
  }
}
