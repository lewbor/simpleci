import $ from 'jquery';
import 'ansi_up';

var ansiparse = function (str) {

    var matchingControl = null,
        matchingData = null,
        matchingText = '',
        ansiState = [],
        result = [],
        state = {},
        eraseChar;

    //
    // General workflow for this thing is:
    // \033\[33mText
    // |     |  |
    // |     |  matchingText
    // |     matchingData
    // matchingControl
    //
    // In further steps we hope it's all going to be fine. It usually is.
    //

    //
    // Erases a char from the output
    //
    eraseChar = function () {
        var index, text;
        if (matchingText.length) {
            matchingText = matchingText.substr(0, matchingText.length - 1);
        }
        else if (result.length) {
            index = result.length - 1;
            text = result[index].text;
            if (text.length === 1) {
                //
                // A result bit was fully deleted, pop it out to simplify the final output
                //
                result.pop();
            }
            else {
                result[index].text = text.substr(0, text.length - 1);
            }
        }
    };

    for (var i = 0; i < str.length; i++) {
        if (matchingControl != null) {
            if (matchingControl == '\x1B' && str[i] == '\[') {
                //
                // We've matched full control code. Lets start matching formating data.
                //

                //
                // "emit" matched text with correct state
                //
                if (matchingText) {
                    state.text = matchingText;
                    result.push(state);
                    state = {};
                    matchingText = "";
                }

                matchingControl = null;
                matchingData = '';
            }
            else {
                //
                // We failed to match anything - most likely a bad control code. We
                // go back to matching regular strings.
                //
                matchingText += matchingControl + str[i];
                matchingControl = null;
            }
            continue;
        }
        else if (matchingData != null) {
            if (str[i] == ';') {
                //
                // `;` separates many formatting codes, for example: `\033[33;43m`
                // means that both `33` and `43` should be applied.
                ansiState.push(matchingData);
                matchingData = '';
            }
            else if (str[i] == 'm') {
                //
                // `m` finished whole formatting code. We can proceed to matching
                // formatted text.
                //
                ansiState.push(matchingData);
                matchingData = null;
                matchingText = '';

                //
                // Convert matched formatting data into user-friendly state object.
                ansiState.forEach(function (ansiCode) {
                    if (ansiparse.foregroundColors[ansiCode]) {
                        state.foreground = ansiparse.foregroundColors[ansiCode];
                    }
                    else if (ansiparse.backgroundColors[ansiCode]) {
                        state.background = ansiparse.backgroundColors[ansiCode];
                    }
                    else if (ansiCode == 39) {
                        delete state.foreground;
                    }
                    else if (ansiCode == 49) {
                        delete state.background;
                    }
                    else if (ansiparse.styles[ansiCode]) {
                        state[ansiparse.styles[ansiCode]] = true;
                    }
                    else if (ansiCode == 22) {
                        state.bold = false;
                    }
                    else if (ansiCode == 23) {
                        state.italic = false;
                    }
                    else if (ansiCode == 24) {
                        state.underline = false;
                    }
                });
                ansiState = [];
            }
            else {
                matchingData += str[i];
            }
            continue;
        }

        if (str[i] == '\x1B') {
            matchingControl = str[i];
        }
        else if (str[i] == '\u0008') {
            eraseChar();
        }
        else {
            matchingText += str[i];
        }
    }

    if (matchingText) {
        state.text = matchingText + (matchingControl ? matchingControl : '');
        result.push(state);
    }
    return result;
};

ansiparse.foregroundColors = {
    '30': 'black',
    '31': 'red',
    '32': 'green',
    '33': 'yellow',
    '34': 'blue',
    '35': 'magenta',
    '36': 'cyan',
    '37': 'white',
    '90': 'grey'
};

ansiparse.backgroundColors = {
    '40': 'black',
    '41': 'red',
    '42': 'green',
    '43': 'yellow',
    '44': 'blue',
    '45': 'magenta',
    '46': 'cyan',
    '47': 'white'
};

ansiparse.styles = {
    '1': 'bold',
    '3': 'italic',
    '4': 'underline'
};

var Log = {};
Log.Deansi = {
    CLEAR_ANSI: /(?:\033)(?:\[0?c|\[[0356]n|\[7[lh]|\[\?25[lh]|\(B|H|\[(?:\d+(;\d+){,2})?G|\[(?:[12])?[JK]|[DM]|\[0K)/gm,
    apply: function (string) {
        var nodes,
            _this = this;
        if (!string) {
            return [];
        }
        string = string.replace(this.CLEAR_ANSI, '');
        nodes = ansiparse(string);
        nodes = nodes.map(function (part) {
            return _this.node(part);
        });
        return nodes;
    },
    node: function (part) {
        var classes, node;
        node = {
            type: 'span',
            text: part.text
        };
        if (classes = this.classes(part)) {
            node["class"] = classes.join(' ');
        }
        return node;
    },
    classes: function (part) {
        var result;
        result = [];
        result = result.concat(this.colors(part));
        if (result.length > 0) {
            return result;
        }
    },
    colors: function (part) {
        var colors;
        colors = [];
        if (part.foreground) {
            colors.push('ansi-fg-' + part.foreground);
        }
        if (part.background) {
            colors.push("ansi-bg-" + part.background);
        }
        if (part.bold) {
            colors.push('ansi-bold');
        }
        if (part.italic) {
            colors.push('ansi-italic');
        }
        if (part.underline) {
            colors.push('ansi-underline');
        }
        return colors;
    },
    hidden: function (part) {
        if (part.text.match(/\r/)) {
            part.text = part.text.replace(/^.*\r/gm, '');
            return true;
        }
    }
};


export default class Terminal {
    constructor(container) {
        this.container = container;
        this.currentLine = '';
        this.currentLineHtml = null;
    }

    appendAndStartNewLIne(line) {
        this.currentLine = this.currentLine + line;
        if (this.currentLineHtml != null) {
            const newHtmlLine = this.stringToLine(this.currentLine);
            this.currentLineHtml.replaceWith(newHtmlLine);
            this.currentLineHtml = newHtmlLine;
        } else {
            const newHtmlLine = this.stringToLine(this.currentLine);
            this.container.append(newHtmlLine);
            this.currentLineHtml = newHtmlLine;
        }

        this.appendNewLine();
        this.currentLine = '';
    }

    appendToEnd(line) {
        this.currentLine = this.currentLine + line;
        if (this.currentLineHtml == null) {
            const newHtmlLine = this.stringToLine(line);
            this.container.append(newHtmlLine);
            this.currentLineHtml = newHtmlLine;
        } else {
            this.ansiToHtml(line).forEach((htmlElement) => {
                this.currentLineHtml.append(htmlElement);
            });
        }
    }

    appendNewLine() {
        const newHtmlLine = this.stringToLine('');
        this.container.append(newHtmlLine);
        this.currentLineHtml = newHtmlLine;
    }

    ansiToHtml(str) {
        var nodes = Log.Deansi.apply(str);
        return nodes.map(function (node) {
            var element = $("<span/>").text(node.text);
            if (node.class) {
                element.attr('class', node.class);
            }
            return element;
        });
    }

    stringToLine(str) {
        var line = $("<p/>");
        line.append("<a/>");
        this.ansiToHtml(str).forEach(function (htmlElement) {
            line.append(htmlElement);
        });

        return line;
    }

    static split(data, callback) {
        let start = 0;
        let current = 0;
        for (; current < data.length; current++) {
            if (data[current] == "\n") {
                const line = data.substr(start, current - start + 1);
                callback(line);
                start = current + 1;
            }
        }
        if (current - start + 1 > 0) {
            const line = data.substr(start, current - start + 1);
            callback(line);
        }
    }


    append(data) {
        if (data.indexOf("\n") == -1) {
            this.appendToEnd(data);
        } else {
            Terminal.split(data, (line) => {
                var timeRegexp = /\[simpleci_time:(.*)]/;
                var res = timeRegexp.exec(line);
                if (res !== null) {
                    line = line.substring(0, res.index);
                    if (line != '') {
                        line = line + "\n";
                    }
                }
                if (line != '') {
                    if (line[line.length - 1] == "\n") {
                        this.appendAndStartNewLIne(line.substring(0, line.length - 1));
                    } else {
                        this.appendToEnd(line);
                    }
                }
            });
        }
    }

}
