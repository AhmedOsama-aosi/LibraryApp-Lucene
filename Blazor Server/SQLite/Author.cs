using System;
using System.Collections.Generic;

namespace LibraryApp.SQLite;

public partial class Author
{
    public long AuthorId { get; set; }

    public string? AuthorName { get; set; }

    public long? DeathNumber { get; set; }

    public string? DeathText { get; set; }
}
