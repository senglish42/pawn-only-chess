package chess

class Chess {
    private var ground = setFigures()
    private var players: Array<String> = arrayOf()
    private var lastPosOfW = ""
    private var lastPosOfB = ""
    private val pawns = arrayOf(8, 8)
    private var isWhite = false
    fun run() {
        introduction()
        game()
    }
    private fun setFigures() : Array<Array<Char>> {
        val ground = Array(8) {Array(8) {' '}}
        ground[1] = Array(8){'W'}
        ground[6] = Array(8){'B'}
        return ground
    }
    private fun introduction() {
        val turn = arrayOf("First", "Second")
        println("Pawns-Only Chess")
        for (i in 0..1) {
            println("${turn[i]} Player's name:")
            this.players += readln()
        }
    }
    private fun game() {
        var count = -1
        val regex = Regex("[a-h][1-8][a-h][1-8]")
        Loop@while (showField() && isGameToBeContinued(++count)) {
            while (true) {
                println("${this.players[if (isWhite) 0 else 1]}'s turn:")
                val str = readln()
                if (str == "exit") break@Loop
                if (isRegexMatches(regex, str) && Move(this, str, if (isWhite) 'W' else 'B').isValid()) break
            }
        }
        println("Bye!")
    }
    private fun showField(): Boolean {
        for (i in 7 downTo 0) {
            line(i + 1)
            for (n in 0..7) {
                print(" ${this.ground[i][n]} |")
            }
            println()
        }
        line(0)
        isWhite = !isWhite
        return true
    }
    private fun line(num: Int) {
        print("  ")
        for (i in 1..8) print("+---")
        if (num > 0) print("+\n${num} |")
        else println("+\n    a   b   c   d   e   f   g   h\n")
    }
    private fun isGameToBeContinued(count: Int): Boolean {
        return if (count < 9) true
        else {
            val blackWins = "Black Wins!"
            val whiteWins = "White Wins!"
            return if (isWhite && lastPosOfB[1] == '1') falseMsg(blackWins)
            else if (!isWhite && lastPosOfW[1] == '8') falseMsg(whiteWins)
            else if (0 in pawns) falseMsg(if (0 == pawns[0]) blackWins else whiteWins)
            else if (isDraw(if (isWhite) 'W' else 'B')) falseMsg("Stalemate!")
            else true
        }
    }
    private fun getRival() = if (isWhite) 'B' else 'W'
    private fun isDraw(player: Char): Boolean {
        return if (pawns[0] <= 4 || pawns[1] <= 4) {
            val rival = getRival()
            val opposite = if (isWhite) 1 else -1
            var count = 0
            for (i in 1..6) {
                for (n in 0..7) {
                    if (ground[i][n] == player && ground[i + opposite][n] == rival) ++count
                }
            }
            if (isWhite) count == pawns[0] else count == pawns[1]
        } else false
    }
    private fun isRegexMatches(regex: Regex, str: String) = if (regex.matches(str)) true else falseMsg("Invalid Input")
    private fun falseMsg(str: String, f: Unit = println(str)) = false

    class Move(private val chess: Chess, private val str: String, private val turn: Char) {
        private val weight = arrayOf(str[0], str[2]).map { it.code - 97 }
        private val height = arrayOf(str[1], str[3]).map { it.code - 49 }
        private val from = chess.ground[height[0]][weight[0]]
        private val to = chess.ground[height[1]][weight[1]]
        private var capture = false
        private var enPassant = false
        fun isValid(): Boolean {
            if (from == turn) {
                if (!isValidInput()) return chess.falseMsg("Invalid Input")
                if (to != ' ' && !capture) return chess.falseMsg(noPawn(if (chess.isWhite) "White" else "Black", str.substring(2, 4)))
                chess.ground[height[0]][weight[0]] = ' '
                chess.ground[height[1]][weight[1]] = turn
                if (enPassant) chess.ground[height[0]][weight[1]] = ' '
                if (chess.isWhite) chess.lastPosOfW = str.substring(2, 4) else chess.lastPosOfB = str.substring(2, 4)
                return true
            }
            return chess.falseMsg(noPawn(if (chess.isWhite) "White" else "Black", str.substring(0, 2)))
        }
        private fun isValidInput(): Boolean {
            if (!isValidDirection() || !isValidGap() || !isValidWidth()) return false
            return if (isTheSameWidth()) !isRivalOnTheField() else {
                return if (isRivalOnTheField()) {
                    capture = true
                    decrementPawn()
                }
                else if (isRivalNearByPlayer() && isEnPassPossible()) {
                    enPassant = true
                    decrementPawn()
                } else false
            }
        }
        private fun decrementPawn(f: Unit = if (chess.isWhite) chess.pawns[1] -= 1 else chess.pawns[0] -= 1) = true
        private fun isTheSameWidth() = str[0].code == str[2].code
        private fun isValidWidth() = kotlin.math.abs(str[0].code - str[2].code) in 0..1
        private fun isRivalOnTheField() = to == chess.getRival()
        private fun isRivalNearByPlayer() = chess.ground[height[0]][weight[1]] == chess.getRival()
        private fun isEnPassPossible() = str.substring(0, 2) == if (chess.isWhite) chess.lastPosOfW else chess.lastPosOfB
        private fun isValidDirection() = if (chess.isWhite) height[0] <= height[1] else height[0] >= height[1]
        private fun isValidGap(): Boolean {
            return if (str[1].code - 49 == if (chess.isWhite) 1 else 6) kotlin.math.abs(str[3].code - str[1].code) in 1..2
            else kotlin.math.abs(str[3].code - str[1].code) == 1
        }
        private fun noPawn(turn: String, pos: String) = "No $turn pawn at $pos"
    }
}

fun main() = Chess().run()