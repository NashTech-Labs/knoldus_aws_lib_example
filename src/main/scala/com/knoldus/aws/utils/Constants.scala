package com.knoldus.aws.utils

object Constants {

  val submitQuestionResponse: String => String = id => s"""{"message": "Question submitted successfully", "id": $id}"""
  val questionNotSubmittedResponse = """{"message": "Question not submitted"}"""
  val updateQuestionResponse = """{"message": "Question updated successfully"}"""
  val noQuestionFoundResponse = """{"message": "Question not found"}"""

  val questionDeletedResponse = """{"message": "Question deleted successfully"}"""
  val questionNotDeletedResponse = """{"message": "Failure occurred deleting the question"}"""
}
