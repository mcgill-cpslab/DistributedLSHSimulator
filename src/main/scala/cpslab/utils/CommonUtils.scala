package cpslab.utils

import java.io.File

/**
 * Created by nanzhu on 15-06-25.
 */
object CommonUtils {
  def buildFileListUnderDirectory(rootPath: String): Seq[String] = {
    val dirObj = new File(rootPath)
    if (dirObj.isDirectory) {
      val regularFiles = dirObj.listFiles()
      regularFiles.view.filter(_.isFile).map(_.getAbsolutePath) ++
        regularFiles.view.filter(_.isDirectory).map(_.getAbsolutePath).flatMap(
          buildFileListUnderDirectory)
    } else {
      Seq(rootPath)
    }
  }
}
