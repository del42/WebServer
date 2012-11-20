<?php
$NUM_COLUMNS = 7;
$NUM_ROWS = 7;
$CELL_WIDTH = 30;
$CELL_HEIGHT = 30;
?>
<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
        <script type="text/javascript" src="js/index.js"></script>
        <script type="text/javascript">
            var numOfColumns = <?= $NUM_COLUMNS ?>;
            var numOfRows=<?= $NUM_ROWS ?>;
        </script>
        <style type="text/css">
            div.body {
                position: relative;
                left: 25%;
                right: 20%;
                width: 60%;
                top: 50px;
            }
            table.puzzleTable {
                position: relative;
                top: 50px;
                border-width: 2px;
                border-spacing: 0px;
                border-style: none;
                border-color: gray;
                border-collapse: collapse;
                background: url("spring.jpg") no-repeat;
                width: 250px;
                height: 300px;
            }
            table.puzzleTable td {
                border-width: 1px;
                border-style: inset;
                border-color: gray;
                background-color: white;
            }
            div.demo {
                position: absolute;
                left: 400px;
                top: 50px;
                border: 1px;
                solid: red;
            }
            label.process {
                position: relative;
                color: red;
                display: none;
            }
            label.hint {
                position: relative;
                top: 30px;
                display: none;
            }
            textarea.question {
                position: relative;
                top: 60px;
                resize: none;
                display: none;
            }
            label.explain{
                position: relative;
                top: 65px;
                display: none;
            }
            textarea.answer {
                position: absolute;
                top: 190px;
                resize: none;
                color: red;
                display: none;
            }
            
            button.demo {
                position: relative;
                top: 140px;
            }

            button.next{
                position: absolute;
                top: 320px;
                right: 175px;
                display: none;
            }
            
        </style>
    </head>
    <body>
        <div class="body">
            <div class="puzzleMap">
                <table class="puzzleTable">
                    <?php
                    for ($i = 0; $i < $NUM_ROWS; $i++) {
                        echo '<tr>';
                        for ($j = 0; $j < $NUM_COLUMNS; $j++) {
                            echo "<td id='cell{$i}{$j}' style='width: {$CELL_WIDTH}px; height: {$CELL_HEIGHT}px'></td>";
                        }
                        echo "</tr>";
                    }
                    ?>
                </table>
            </div>
            <div class="demo">
                <!-- Demo -->
                <p id="pDemo">Description of the game.</p>
                <label id="process" class="process">Step</label><br/>
                <label id="hint" class="hint"></label><br/>
                <textarea id="question" class="question" rows="3" cols="25" readonly="readonly">Vq dg qt pqv vq dg</textarea><br/>
                <textarea id="answer" class="answer" rows="3" cols="25" readonly="readonly"></textarea>
                <textarea id="answer1" class="answer" rows="3" cols="25" readonly="readonly" style="top: 190px"></textarea>
                <textarea id="answer2" class="answer" rows="3" cols="25" readonly="readonly" style="top: 190px"></textarea>
                 <textarea id="answer3" class="answer" rows="3" cols="25" readonly="readonly" style="top: 190px"></textarea>
                <label id="points" class="points"></label><br/>
                 <button id="buttonDemo" class="demo">See Demo</button>
            </div>
                <button id="next" class="next" onclick="showNext()">Next</button>
        </div>
    </body>
</html>