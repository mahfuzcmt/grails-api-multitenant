package com.bitsoft.st.utils

class EmailTemplateParser {
    static class EOF extends Exception {
        public EOF() {}
    }

    private static convertToken(String token, Map binding, Boolean keepMissingToken) {
        String spliter = token.contains(">") ? ">" : "\\."
        String[] mapParts = token.split(spliter)
        def value = ""
        for(String part : mapParts) {
            value = binding[part]
            if(value instanceof Map) {
                binding = value
            } else {
                break
            }
        }
        return ((keepMissingToken && value == null) ? "%${token}%" : (value ?: ""))
    }

    static String parse(String input, Map bindings, StringBuilder output = new StringBuilder(), Boolean keepMissingToken = false) {
        StringReader reader = new StringReader(input);
        String globalCondition
        try {
            while (true) {
                skipUpTo(reader, (char) '%', output)
                String token = captureUpTo(reader, (char) '%');
                String[] tokens;
                if (token == "") {
                    output << '%';
                } else if((tokens=token.split(":")).size() > 1 && tokens[0] == "if" ) {
                    String block = captureUpTo(reader, "if");
                    def condition = convertToken(tokens[1], bindings, keepMissingToken);
                    if (condition && condition != "%${tokens[1]}%") {
                        parse(block, bindings, output)
                    } else if (keepMissingToken && condition == "%${tokens[1]}%") {
                        output << "%${token}%";
                        output << block;
                        output << "%if%";
                    }
                    globalCondition = condition
                } else if(tokens[0] == "else") {
                    String block = captureUpTo(reader, "else");
                    def condition = globalCondition
                    if(!condition && !keepMissingToken) {
                        parse(block, bindings, output)
                    }
                } else if (tokens[0] == "each") {
                    String block = captureUpTo(reader, "each");
                    def repeatable = convertToken(tokens[1], bindings, keepMissingToken)
                    if (repeatable instanceof List) {
                        String valueVar = "value";
                        if (tokens.size() > 2) {
                            valueVar = tokens[2];
                        }
                        String indexVar = "index";
                        if (tokens.size() > 3) {
                            indexVar = tokens[3];
                        }
                        repeatable.eachWithIndex { def entry, int i ->
                            Map newScope = bindings.clone();
                            newScope[valueVar] = entry;
                            newScope[indexVar] = i
                            parse(block, newScope, output)
                        }
                    } else if (keepMissingToken) {
                        output << "%${token}%";
                        output << block;
                        output << "%each%";
                    }

                } else {
                    output << convertToken(token, bindings, keepMissingToken)
                }
            }
        } catch (EOF e) {}
        return output.toString();
    }

    private static char skipUpTo(Reader reader, char lookup, StringBuilder out) {
        char c;
        while(true) {
            c = reader.read();
            if(c == (char)-1) {
                throw new EOF();
            }
            if(c == lookup) {
                return c;
            }
            out << c
        }
    }

    static String captureUpTo(Reader reader, char lookup) {
        StringBuilder builder = new StringBuilder();
        char c;
        while(true) {
            c = reader.read();
            if(c == (char)-1) {
                throw new EOF();
            }
            if(c == lookup) {
                return builder.toString();
            }
            builder << c;
        }
    }

    static String captureUpTo(Reader reader, String endTag) {
        StringBuilder block = new StringBuilder();
        Integer startTagCount = 0
        while (true) {
            skipUpTo(reader, (char) '%', block)
            String token = captureUpTo(reader, (char) '%');
            List tokens
            if (token == endTag && startTagCount == 0) {
                break;
            } else if(token == endTag) {
                startTagCount--
            } else if((tokens = token.split(":")).size() > 1 && tokens[0] ==  endTag) {
                startTagCount++
            }
            block << "%${token}%";
        }
        return block;
    }
}
