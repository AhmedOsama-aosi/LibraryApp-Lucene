using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class Setting
{
    public short RowId { get; set; }

    public string Key { get; set; } = null!;

    public string Value { get; set; } = null!;

    public string Default { get; set; } = null!;
}
