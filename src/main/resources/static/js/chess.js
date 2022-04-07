let before = "";
let after = "";

const moveUrl = `/move`;
const initialize = () => {
    document.querySelectorAll('.piece-image')
        .forEach(square => square.addEventListener('click', cellClick));
}

const cellClick = (event) => {
    let currentPieceImg = event.target;

    if (before === "") {
        before = currentPieceImg;
        before.style.backgroundColor = '#ffb9b9';
        return;
    }

    if (before !== "" && after === "") {
        after = currentPieceImg;
        after.style.backgroundColor = '#fc8383';

        postMoveCommandWith(before, after)

        before = "";
        after = "";

        return;
    }
}

function postMoveCommandWith(before, after) {
    let f = document.createElement("form"); // form 엘리멘트 생성
    f.setAttribute("method", "post"); // method 속성을 post로 설정
    f.setAttribute("action", "/move"); // submit했을 때 무슨 동작을 할 것인지 설정
    document.body.appendChild(f);

    let i = document.createElement("input"); // input 엘리멘트 생성
    i.setAttribute("type", "hidden"); // type 속성을 hidden으로 설정
    i.setAttribute("name", "command"); // name 속성을 'm_nickname'으로 설정
    i.setAttribute("value", "move " + before.id + " " + after.id); // value 속성을 neilong에 담겨있는 값으로 설정
    f.appendChild(i); // form 엘리멘트에 input 엘리멘트 추가
    f.submit();
}

initialize();
