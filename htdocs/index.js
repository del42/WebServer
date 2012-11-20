var cellIds = new Array();
var intervalId = -1;
var step = 0;
var firstSolution = "22 17 04 07 17 20 16 17 22 22 17 04 07";
var secondSolution = "20 15 02 05 15 18 14 15 20 20 15 02 05";
var famousSentence = "To be not to be";
$(document).ready( function(){
    loadAllCellIds();
    intervalId = window.setInterval("flipCell()", 1000);
    $("#buttonDemo").click(function(){
        showDemo();
    });
});

function flipCell(){
    var cellId = getRandomCellId();
    if ( cellId == "" ) return;
    $(cellId).fadeTo("slow", 0);
}

/*
 * Generate a unduplicated cell id randomly.
 */
function getRandomCellId(){
    var length = cellIds.length;
    if ( length == 0 ){ 
        window.clearInterval(intervalId);
        return "";
    }
    var randomNumber = Math.floor(Math.random() * length);
    var id = cellIds[randomNumber];
    cellIds.splice(randomNumber, 1);
    return id;
}

function loadAllCellIds(){
    for ( var i = 0; i < numOfRows; i++ ){
        for( var j = 0; j < numOfColumns; j++){
            cellIds.push("#cell"+i+j);
        }
    }
}

function showDemo(){
    window.clearInterval(intervalId);
    hideImage();
    stepOne();
}

function stepOne(){
    $("#pDemo").hide();
    $("#answer1").hide();
    $("#answer2").hide();
    $("#answer3").hide();
    $("#buttonDemo").hide();
    $("#question").show();
    $("#process").show();
    $("#hint").show();
    $("#answer").show();
    $("#next").show();
    document.getElementById("process").innerHTML = "Step 1:";
    document.getElementById("hint").innerHTML = "Decrypt following charaters";
}


function hideImage(){
    
    cellIds = new Array();
    for ( var i = 0; i < numOfRows; i++ ){
        for( var j = 0; j < numOfColumns; j++){
            var id = "#cell"+i+j;
            cellIds.push(id);
            $(id).css("opacity", "1");
        }
    }
}

/*
 * Animate answer to textArea
 */
function animateText(textArea, text, duaration){
    $("#next").hide();
    textArea.value = "";
    var length = text.length;
    var index = 0;
    intervalId = window.setInterval(function(){
        if ( index >= length ){
            window.clearInterval(intervalId);
            $("#next").show();        
        }
        textArea.value += text.charAt(index);
        index++;
    }, duaration);
    
}
function showNext(){

    if( step === 11 ){
        return;
    }
    step++;
    showStep();
}

function showStep(){
    switch ( step ){
        case 1:
            animateText(document.getElementById("answer"), "Solution is algebraic \u2013 first convert to numeric", 100);
            break;
        case 2:
            animateText(document.getElementById("answer"), firstSolution, 100);
            break;
        case 3:
            stepThree();
            break;
        case 4:
            animateText(document.getElementById("answer1"), "Find solution \u2013 in this case subtract 2", 100);
            break;
        case 5:
            animateText(document.getElementById("answer1"), secondSolution, 150);
            break;
        case 6:
            stepSix();
            break;
        case 7:
            animateText(document.getElementById("answer2"), famousSentence, 150);
            break;
        case 8:
            stepEight();
            break;
        case 9:
            animateText(document.getElementById("answer3"), "Shakspeare", 150);
            break;
        case 10:
            document.getElementById("answer3").value = "Congrad! you earn 5 points";
            flipCell();
            document.getElementById("next").innerHTML = "Play Again";
            break;
        case 11:
            document.getElementById("next").innerHTML = "Next";
            step = 0;
            repositionAnswer();
            stepOne();
            break;
    }
    
}

function stepThree(){
    document.getElementById("process").innerHTML = "Step 2:";
    document.getElementById("hint").innerHTML = "Arithmetic solutions";
    $("#question").fadeOut(300, function(){
        $("#answer").animate({
            top: "-=90"
        }, 1000, function(){
            $(this).css("color", "black");
            $("#answer1").fadeIn(500);
        });
    });

}

function stepSix(){
    document.getElementById("process").innerHTML = "Step 3:";
    document.getElementById("hint").innerHTML = "Find a famous sentence";
    $("#answer").fadeOut(300, function(){
        $("#answer1").animate({
            top: "-=90"
        }, 1000, function(){
            $(this).css("color", "black");
            $("#answer2").fadeIn(500);
        });
    });
}

function stepEight(){
    document.getElementById("process").innerHTML = "Step 4:";
    document.getElementById("hint").innerHTML = "Find the author of the sentence";
    $("#answer1").fadeOut(300, function(){
        $("#answer2").animate({
            top: "-=90"
        }, 1000, function(){
            $(this).css("color", "black");
            $("#answer3").fadeIn(500);
        });
    });
}

function repositionAnswer(){
    $("#answer").css({"top": "190px", "color": "red"});
    $("#answer1").css({"top": "190px", "color": "red"});
    $("#answer2").css({"top": "190px", "color": "red"});
    $("#answer3").css({"top": "190px", "color": "red"});
    document.getElementById("answer").value = "";
    document.getElementById("answer1").value = "";
    document.getElementById("answer2").value = "";
    document.getElementById("answer3").value = "";
}
