package watson.dialog.tools.model.domain;

public interface IType {

  public enum SelectionType {
    SEQUENTIAL,
    RANDOM;

    public String value() {
      return name();
    }

    public static SelectionType fromValue(String v) {
      return valueOf(v);
    }
  }

  public enum PromptType {
    VARIATIONS,
    NLG;

    public String value() {
        return name();
    }

    public static PromptType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum GrammarType {
    VARIATIONS,
    JSGF,
    GRXML,
    AQL,
    DICT;

    public String value() {
        return name();
    }

    public static GrammarType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum MatchType {
    ALL,
    ANY;

    public String value() {
        return name();
    }

    public static MatchType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum ConditionOperatorType {
    EQUALS,
    CONTAINS,
    MATCHES_PATTERN,
    LESS_THEN,
    GREATER_THEN,
    EQUAL_TO_YES,
    EQUAL_TO_NO,
    IS_BLANK,
    HAS_VALUE;

    public String value() {
        return name();
    }

    public static ConditionOperatorType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum ActionOperatorType {
    DO_NOTHING_STR,
    SET_TO,
    SET_TO_USER_INPUT,
    SET_TO_USER_INPUT_CORRECTED,
    INCREMENT_BY,
    DECREMENT_BY,
    SET_TO_YES,
    SET_TO_NO,
    SET_AS_USER_INPUT,
    SET_TO_BLANK,
    APPEND;

    public String value() {
        return name();
    }

    public static ActionOperatorType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum VarFolderType {
    VAR,
    CONST;

    public String value() {
        return name();
    }

    public static VarFolderType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum VarType {
    TEXT,
    NUMBER,
    YESNO,
    DATETIME,
    TAG,
    CONST;

    public String value() {
        return name();
    }

    public static VarType fromValue(String v) {
        return valueOf(v);
    }
  }

  public enum SettingType {
    USER;

    public String value() {
        return name();
    }

    public static SettingType fromValue(String v) {
        return valueOf(v);
    }
  }
}
