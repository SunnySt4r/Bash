package com.example;

import com.example.command.Cat;
import com.example.command.Command;
import com.example.command.Echo;
import com.example.command.Exit;
import com.example.command.Grep;
import com.example.command.Ls;
import com.example.utils.WrongCommandExeption;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    void parse_SingleEchoCommand_ReturnsEchoCommand() throws WrongCommandExeption {
        String input = "echo hello world";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
    }

    @Test
    void parse_SingleExitCommand_ReturnsExitCommand() throws WrongCommandExeption {
        String input = "exit";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Exit.class, commands.getFirst());
    }

    @Test
    void parse_SingleCatCommandWithArguments_ReturnsCatCommandWithArguments() throws WrongCommandExeption {
        String input = "cat file1.txt file2.txt";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Cat.class, commands.getFirst());
    }

    @Test
    void parse_SingleLsCommand_ReturnsLsCommand() throws WrongCommandExeption {
        String input = "ls";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Ls.class, commands.getFirst());
    }

    @Test
    void parse_SingleGrepCommandWithArguments_ReturnsGrepCommandWithArguments() throws WrongCommandExeption {
        String input = "grep -i pattern file.txt";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Grep.class, commands.getFirst());
    }

    @Test
    void parse_MultipleCommandsWithPipe_ReturnsListOfCommands() throws WrongCommandExeption {
        String input = "echo hello | grep llo | cat";
        List<Command> commands = Parser.parse(input);
        assertEquals(3, commands.size());
        assertInstanceOf(Echo.class, commands.get(0));
        assertInstanceOf(Grep.class, commands.get(1));
        assertInstanceOf(Cat.class, commands.get(2));
    }

    @Test
    void parse_UnknownCommand_ThrowsWrongCommandException() {
        String input = "unknown_command arg1 arg2";
        assertThrows(WrongCommandExeption.class, () -> Parser.parse(input));
    }

    @Test
    void parse_CommandWithExtraSpaces_ParsesCorrectly() throws WrongCommandExeption {
        String input = "  echo   hello  world   ";
        List<Command> commands = Parser.parse(input);
        assertEquals(1, commands.size());
        assertInstanceOf(Echo.class, commands.getFirst());
    }

    @Test
    void parse_PipeWithExtraSpaces_ParsesCorrectly() throws WrongCommandExeption {
        String input = "echo hello |  grep world ";
        List<Command> commands = Parser.parse(input);
        assertEquals(2, commands.size());
        assertInstanceOf(Echo.class, commands.get(0));
        assertInstanceOf(Grep.class, commands.get(1));
    }
}
