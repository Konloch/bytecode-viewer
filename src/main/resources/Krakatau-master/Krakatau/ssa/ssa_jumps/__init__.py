from .base import BaseJump

from .onexception import OnException
from .goto import Goto
from .ifcmp import If
from .exit import Return, Rethrow
from .switch import Switch

from . import placeholder
OnAbscond = Ret = placeholder.Placeholder
