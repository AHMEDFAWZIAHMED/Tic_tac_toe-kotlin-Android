package a7m3d.game.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private val squares = arrayListOf<AppCompatImageView>()
    private val userPlayer = arrayListOf<Int>()
    private val computerPlayer = arrayListOf<Int>()
    private val squaresID = listOf(
        R.id.square_11, R.id.square_12, R.id.square_13,
        R.id.square_21, R.id.square_22, R.id.square_23,
        R.id.square_31, R.id.square_32, R.id.square_33
    )
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in squaresID.indices) {
            val square = findViewById<AppCompatImageView>(squaresID[i])
            square.setOnClickListener { userChoice(i) }
            squares.add(square)
        }
    }

    private fun userChoice(indx: Int) {
        disableSquares(true)
        userPlayer.add(indx)
        chosenSquare(indx, R.drawable.player_one_mark, Color.BLUE)
        checkResult("user")
    }

    private fun computerChoice() {
        val choice = mediumChoice()
        computerPlayer.add(choice)
        Handler(Looper.getMainLooper()).postDelayed({
            chosenSquare(choice, R.drawable.player_two_mark, Color.BLUE)
            checkResult("computer")
        }, 500)
    }

    private fun chosenSquare(indx: Int, resId: Int, color: Int) {
        squares[indx].setImageResource(resId)
        squares[indx].setColorFilter(color)
        squares[indx].background = ContextCompat.getDrawable(
            this, R.drawable.square_background_clicked)
        val param = squares[indx].layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(2, 0, 2, 0)
        squares[indx].layoutParams = param
        squares[indx].isClickable = false
    }

    private fun disableSquares(boolean: Boolean) {
        for (indx in squares.indices) {
            if (indx !in userPlayer || indx !in computerPlayer) {
                squares[indx].isClickable = !boolean
            }
        }
    }

    private fun checkResult(player: String) {
        count++
        when (player) {
            "user" -> {
                if (userPlayer.size > 2) {
                    val patternList = patternCheck(userPlayer)
                    if (patternList.isNotEmpty()) {
                        for (i in patternList) {
                            chosenSquare(i, R.drawable.player_one_mark, Color.WHITE)
                        }
                        gameOver("You win!")
                        return
                    }
                }
                if (count == 9) {
                    gameOver("Draw")
                    return
                }
                computerChoice()
            }
            "computer" -> {
                if (computerPlayer.size > 2) {
                    val patternList = patternCheck(computerPlayer)
                    if (patternList.isNotEmpty()) {
                        for (i in patternList) {
                            chosenSquare(i, R.drawable.player_two_mark, Color.WHITE)
                        }
                        gameOver("Computer win")
                        return
                    }
                }
                if (count == 9) {
                    gameOver("Draw")
                    return
                }
                disableSquares(false)
            }
        }
    }

    private fun patternCheck(playerList: ArrayList<Int>): List<Int> {
        val columns = listOf(0, 1, 2)
        val rows = listOf(0, 3, 6)
        val fDiagonal = listOf(0, 4, 8)
        val sDiagonal = listOf(2, 4, 6)
        for (index in playerList) {
            if (index in columns) {
                if ((index+3) in playerList && (index+6) in playerList) {
                    return listOf(index, index+3, index+6)
                }
            }
            if (index in rows) {
                if ((index+1) in playerList && (index+2) in playerList) {
                    return listOf(index, index+1, index+2)
                }
            }
            if (index in fDiagonal) {
                if (fDiagonal[0] in playerList && fDiagonal[1] in playerList
                    && fDiagonal[2] in playerList) {
                    return listOf(fDiagonal[0], fDiagonal[1], fDiagonal[2])
                }
            }
            if (index in sDiagonal) {
                if (sDiagonal[0] in playerList && sDiagonal[1] in playerList
                    && sDiagonal[2] in playerList) {
                    return listOf(sDiagonal[0], sDiagonal[1], sDiagonal[2])
                }
            }
        }
        return emptyList()
    }

    private fun mediumChoice(): Int {
        if (userPlayer.size > 1) {
            val choiceList = (0..8).filterNot {
                it in userPlayer || it in computerPlayer
            }
            val userCopy = ArrayList(userPlayer)
            for (choice in choiceList) {
                userCopy.add(choice)
                if (patternCheck(userCopy).isNotEmpty()) return choice
                userCopy.remove(choice)
            }
        }
        return generateSequence {
            Random.nextInt(0..8)
        }.distinct().filterNot { it in userPlayer || it in computerPlayer }
            .take(1).toList()[0]
    }

    private fun gameOver(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle(msg)
        dialog.setNeutralButton("Restart") { _, _ -> recreate() }
        dialog.setPositiveButton("Exit") { _, _ -> finish() }
        val alertDialog = dialog.create()
        val dWindow = alertDialog.window
        val wlp = dWindow?.attributes
        wlp?.gravity = Gravity.BOTTOM
        Handler(Looper.getMainLooper()).postDelayed({alertDialog.show()}, 500)
    }
}